package com.patient.system.backend.service;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.patient.system.backend.repository.AidBoxRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.repository.OpenSearchRepository;

@Service
public class SearchService {
    @Autowired
    private OpenSearchRepository openSearchRepository;

    @Autowired
    private AidBoxRepository aidBoxRepository;
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    HttpClient client;  
    HttpResponse<String> response;

    JSONObject jsonObject;
    String jsonPayload;

    //Create a new patient
    public Patient indexPatient(Patient patient){
        try {
            //HTTP client only for OpenSearch which trust all the SSL certificates
            client = HttpClient.newBuilder().sslContext(createInsecureSSLContext()).build();
            response = client.send(openSearchRepository.indexPatientToOpenSearch(patient), HttpResponse.BodyHandlers.ofString());

            System.out.println("Request to OpenSearch status code: " + response.statusCode());
            
            return patient;

        } catch (Exception e) {
            System.out.println("Patient was not created!");
            System.out.println(HttpStatus.INTERNAL_SERVER_ERROR);

            return null;
        } 

    }

    //Search a patient
    public Patient searchPatient(String name){
        try {
            //HTTP client only for OpenSearch which trust all the SSL certificates
            client = HttpClient.newBuilder().sslContext(createInsecureSSLContext()).build();  
            
            Patient patient = new Patient();

            response = client.send(openSearchRepository.searchPatientFromOpenSearch(name), HttpResponse.BodyHandlers.ofString());

            jsonObject = new JSONObject(response.body());
            System.out.println(jsonObject);

            JSONArray openSearchPatientDetailsArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
            System.out.println(openSearchPatientDetailsArray);

            if(openSearchPatientDetailsArray.length() > 0){
                JSONObject hitObject = openSearchPatientDetailsArray.getJSONObject(0);
                JSONObject sourceObject = hitObject.getJSONObject("_source");

                patient.setId(sourceObject.getString("id"));
                patient.setName(sourceObject.getString("name"));
                patient.setGender(sourceObject.getString("gender"));
                patient.setDateOfBirth(sourceObject.getString("dateOfBirth"));
                patient.setPhoneNumber(sourceObject.getString("phoneNumber"));

                System.out.println("Patient was found!");
                logger.info("Patient was found");
                return patient;

            } else{
                System.out.println("Patient was not found!");
                logger.info("Patient was not found");
                return null;

            }
        } catch (Exception e) {
            System.out.println("Error in request to OpenSearch!");
          //  System.out.println(e);
            System.out.println(e.getMessage());
            logger.error("Error occurred while searching patient: {}", e.getMessage(), e);
            System.out.println(HttpStatus.INTERNAL_SERVER_ERROR);
            return null;
        } 

    }

    //UPDATE
    public Patient updatePatient(Patient patient){
        try{
            client = HttpClient.newBuilder().sslContext(createInsecureSSLContext()).build();

            //1) Update the patient in AidBox
            response = client.send(aidBoxRepository.updatePatientAidBox(patient), HttpResponse.BodyHandlers.ofString());

            //2) Update the patient in OpenSearch
            response = client.send(openSearchRepository.updatePatientToOpenSearch(patient), HttpResponse.BodyHandlers.ofString());
            logger.info("Patient updated successfully: {}", patient);

            return patient;

        } catch(Exception e) {
            logger.error("Error occurred while updating patient: {}", e.getMessage(), e);
            return null;
        }
    }

    // Disable SSL verification
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
