package com.patient.system.backend.repository;

import java.net.http.HttpRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.patient.system.backend.entity.Patient;
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
    @Value("${openSearchUrlBase}")
    private String openSearchUrlBase;

    //Create index patient HTTP request to OpenSearch
    public HttpRequest createIndexPatientOpenSearch(){
            String jsonPayload = """
                    {
                      "settings": {
                        "index": {
                          "number_of_shards": 1,
                          "number_of_replicas": 1
                        }
                      },
                      "mappings": {
                        "properties": {
                          "id": {
                            "type": "text"
                          },
                          "name": {
                            "type": "text"
                          },
                          "gender": {
                            "type": "text"
                          },
                          "dateOfBirth": {
                            "type": "text"
                          },
                          "phoneNumber": {
                            "type": "text"
                          }
                        }
                      }
                    }
                    """;

            System.out.println(jsonPayload);

            //OpenSearch requires that the basic authentication header to be encoded to base65
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + encodedAuth;

            //Register the patient to OpenSearch
            request = HttpRequest.newBuilder()
                    .uri(URI.create(openSearchUrlBase.concat("/patients")))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader) //OpenSearch auth.
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            return request;

    }


    //Index a patient HTTP request to OpenSearch
    public HttpRequest indexPatientToOpenSearch(Patient patient){
        String jsonPayload = String.format( """
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
        System.out.println(jsonPayload);
        //OpenSearch requires that the basic authentication header to be encoded to base65
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;
        
        //Register the patient to OpenSearch
        request = HttpRequest.newBuilder()
            .uri(URI.create(openSearchUrlBase.concat("/patients/_doc/"+patient.getId()+"?pretty")))
            .header("Content-Type", "application/json")
            .header("Authorization", authHeader) //OpenSearch auth.
            .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
            .build();
        return request;
    }

    //Search a patient HTTP request to OpenSearch
    public HttpRequest searchPatientFromOpenSearch(String patientName){

        String jsonPayload = String.format( """
                {
                    "query": {
                        "multi_match": {
                            "query": %s,
                            "type": "phrase_prefix",
                            "fields": ["name", "phoneNumber"]
                        }
                    }
                }
                """,
                patientName);

        //OpenSearch requires that the basic authentication header to be encoded to base65
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;
        
        //Register the patient to OpenSearch
        request = HttpRequest.newBuilder()
            .uri(URI.create(openSearchUrlBase.concat("/patients/_search?&format=json&filter_path=hits.hits._source")))
            .header("Content-Type", "application/json")
            .header("Authorization", authHeader) //OpenSearch auth.
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
            .build();

        return request;
    }

    //Update a patient details
    public HttpRequest updatePatientToOpenSearch(Patient patient){
        String jsonPayload = String.format( """
                {
                    "doc":{
                        "id" : "%s",
                        "name" : "%s",
                        "gender" : "%s",                
                        "dateOfBirth" : "%s",
                        "phoneNumber" : "%s"
                    }
                }
                """,
                patient.getId(),
                patient.getName(),
                patient.getGender(),
                patient.getDateOfBirth(),
                patient.getPhoneNumber());

        //OpenSearch requires that the basic authentication header to be encoded to base65
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        //Register the patient to OpenSearch
        request = HttpRequest.newBuilder()
                .uri(URI.create(openSearchUrlBase.concat("/patients/_update/"+ patient.getId())))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authHeader) //OpenSearch auth.
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
                .build();

        return request;
    }
}
