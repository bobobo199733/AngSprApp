package com.patient.system.backend.repository;

import java.net.http.HttpRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class OpenSearchRepository {
    
    HttpRequest request;

    //OpenSearch credentials
    @Value("${openSearchUsername}")
    String username;
    @Value("${openSearchPassword}")
    String password;
    
    //Index a patient HTTP request to OpenSearch
    public HttpRequest indexPatientToOpenSearch(String jsonPayload, String patientId){
        //OpenSearch requires that the basic authentication header to be encoded to base65
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;
        
        //Register the patient to AidBox
        request = HttpRequest.newBuilder()
        .uri(URI.create("https://localhost:9200/patients/_doc/"+patientId+"?pretty"))
        .header("Content-Type", "application/json")
        .header("Authorization", authHeader) //OpenSearch auth.
        .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
        .build();

        return request;
    }

    //Search a patient HTTP request to OpenSearch
    public HttpRequest searchPatientFromOpenSearch(String patientName){
        //OpenSearch requires that the basic authentication header to be encoded to base65
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;
        
        //Register the patient to AidBox
        request = HttpRequest.newBuilder()
        .uri(URI.create("https://localhost:9200/patients/_search?q=name:%22".concat(patientName).concat("%22?case_insensitive=true?pretty")))
        .header("Authorization", authHeader) //OpenSearch auth.
        .GET()
        .build();

        return request;
    }
}
