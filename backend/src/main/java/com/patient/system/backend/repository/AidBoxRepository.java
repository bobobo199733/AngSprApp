package com.patient.system.backend.repository;

import java.net.http.HttpRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class AidBoxRepository {
    
    HttpRequest request;

    //OpenSearch credentials
    @Value("${aidboxSecret}")
    private String password;


    public HttpRequest insertPatientToAidBox(String jsonPayload){
            
        //Register the patient to AidBox
        request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8888/fhir/Patient/"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Authorization", "Basic ".concat(password)) //Aidbox auth.
        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
        .build();

        return request;

    }

}
