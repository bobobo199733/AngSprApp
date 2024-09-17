package com.patient.system.backend.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.repository.DataRepository;

@Service
public class DataService{

    @Autowired
    private DataRepository dataRepository;

    //The HTTP client will send a post request in order to save the new patient in Aidbox
    HttpClient client = HttpClient.newHttpClient();  
    HttpResponse<String> response;

    //The JSONObject will *store* the response == the new patient's data
    //It was needed to get the value of the key "id" 
    JSONObject jsonObject;

    //OpenSearch credentials
    String username = "admin";
    String password = "punisher9733ABCDE!";
    //OpenSearch requires that the basic authentication header to be encoded to base65
    String auth = username + ":" + password;
    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    String authHeader = "Basic " + encodedAuth;
    
    //Constructor
    public DataService(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }
    
    //Get the patient's list from AidBox
    public List<Patient> seePatients(){
        try {


            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8888/fhir/Patient/"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Basic YmFzaWM6c2VjcmV0") //Aidbox auth.
            .GET()
            .build();
    
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonObject = new JSONObject(response.body());
            
            Patient patient = new Patient();
            patient.setId(jsonObject.getString("id"));
            patient.setName(jsonObject.getJSONObject("name").getJSONObject("text").getString("text"));
            patient.setGender(jsonObject.getString("gender"));
            patient.setDateOfBirth(jsonObject.getString("birthDate"));
            patient.setPhoneNumber(jsonObject.getJSONObject("telecom").getString("value"));

            

            System.out.println("Patient: " + patient.getId() + " successfully created!");
            System.out.println(HttpStatus.CREATED);

            return (List<Patient>) dataRepository.seePatients();
            

        } catch (Exception e) {
            System.out.println("Patient was not created!");
            System.out.println(HttpStatus.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    //Create a new patient
    public Patient addPatient(Patient patient){
        try {
            //Patient FHIR format JSON payload
            String jsonPayload = String.format( """
                    {
                      "resourceType": "Patient",
                      "name": [
                          {
                            "text": "%s"
                          }
                      ],
                      "gender": "%s",
                      "birthDate": "%s",
                      "telecom": [
                          {
                            "system": "phone",
                            "value": "%s"
                          }
                      ]
                    }
                    """,
                    patient.getName(),
                    patient.getGender(),
                    patient.getDateOfBirth(),
                    patient.getPhoneNumber());
    
            //Register the patient to AidBox
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8888/fhir/Patient/"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Basic YmFzaWM6c2VjcmV0") //Aidbox auth.
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
            .build();
    
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            jsonObject = new JSONObject(response.body());
            patient.setId(jsonObject.getString("id"));

            System.out.println("Patient: " + patient.getId() + " successfully created!");
            System.out.println(HttpStatus.CREATED);
            
            //Another HTTP client only for OpenSearch which trust all the SSL certificates
            HttpClient clientIgnoreSSLForOpenSearch = HttpClient.newBuilder().sslContext(createInsecureSSLContext()).build();  
            // Define the JSON payload for OpenSearch
            jsonPayload = String.format("""
                {
                    "id" : "%s",
                    "name" : "%s",
                    "gender" : "%s",                
                    "dateOfBirth" : "%s",
                    "phoneNumber" : "%s"
                }
                """,
                patient.getId(),
                patient.getName(),
                patient.getGender(),
                patient.getDateOfBirth(),
                patient.getPhoneNumber());

            HttpRequest requestOpenSearch = HttpRequest.newBuilder()
            .uri(URI.create("https://localhost:9200/patients/_doc/"+patient.getId()+"?pretty"))
            .header("Content-Type", "application/json")
            .header("Authorization", authHeader) //Aidbox auth.
            .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();
            response = clientIgnoreSSLForOpenSearch.send(requestOpenSearch, HttpResponse.BodyHandlers.ofString());

            System.out.println("Request to OpenSearch status code: " + response.statusCode());
            
            return dataRepository.addPatient(patient);

        } catch (Exception e) {
            System.out.println("Patient was not created!");
            System.out.println(HttpStatus.INTERNAL_SERVER_ERROR);
            return null;
        } 

    }

    
    //UPDATE
   // public Patient updatePatient(Patient patient, Long patientId){
     //   return dataRepository.updatePatient(patient, patientId);
   // }

    //DELETE
   // public void deletePatient(Patient patient, Long patientId){
     //   dataRepository.deletePatient(patient, patientId);
    //}

    // Disable SSL verification - I'm connect to OpenSearch from localhost
    // I'm using a self-signed SSL certificate, which is not trusted by the Java HttpClient.
    private static SSLContext createInsecureSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
        return sslContext;
    }
}
