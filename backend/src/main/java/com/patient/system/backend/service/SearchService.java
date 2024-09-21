package com.patient.system.backend.service;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.patient.system.backend.control.SearchControl;
import com.patient.system.backend.repository.AidBoxRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.repository.OpenSearchRepository;
import com.patient.system.backend.repository.SearchRepository;

@Service
public class SearchService {
    @Autowired
    private SearchRepository searchRepository;

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

            response = client.send(openSearchRepository.indexPatientToOpenSearch(jsonPayload,patient.getId()), HttpResponse.BodyHandlers.ofString());

            System.out.println("Request to OpenSearch status code: " + response.statusCode());
            
            return searchRepository.indexPatient(patient);

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
            
            String encodedPatientName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());

            response = client.send(openSearchRepository.searchPatientFromOpenSearch(encodedPatientName), HttpResponse.BodyHandlers.ofString());
          
            jsonObject = new JSONObject(response.body());
            System.out.println(jsonObject);

            JSONArray openSearchPatientDetailsArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
            
            if(openSearchPatientDetailsArray.length() > 0){
                
                //For future, in case two patients have the same name
                //IMPORTANT! In case the use case of multiple patients under the same name is implemented, the return type must be a list of patients e.g., List<Patient>
                for(int i=0; i<openSearchPatientDetailsArray.length(); i++){
                    JSONObject hitObject = openSearchPatientDetailsArray.getJSONObject(i);
                    JSONObject sourceObject = hitObject.getJSONObject("_source");

                    patient.setId(sourceObject.getString("id"));
                    patient.setName(sourceObject.getString("name"));
                    patient.setGender(sourceObject.getString("gender"));
                    patient.setDateOfBirth(sourceObject.getString("dateOfBirth"));
                    patient.setPhoneNumber(sourceObject.getString("phoneNumber"));
                    
                }

                System.out.println("Patient was found!");
                logger.info("Patient was found");
                return searchRepository.indexPatient(patient);
            
            } else{
            
                System.out.println("Patient was not found!");
                logger.info("Patient was not found");
                return null;
            
            }
        } catch (Exception e) {
            System.out.println("Error in request to OpenSearch!");
            System.out.println(e);
            System.out.println(e.getMessage());
            logger.error("Error occurred while upserting patient: {}", e.getMessage(), e);
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

            return searchRepository.updatePatient(patient);

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
