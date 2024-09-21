package com.patient.system.backend.repository;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

import com.patient.system.backend.entity.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class AidBoxRepository {
    
    HttpRequest request;

    //OpenSearch credentials
    @Value("${aidboxSecret}")
    private String password;
    @Value("${aidboxDefaultURL}")
    private String aidBoxURL;

    public HttpRequest insertPatientToAidBox(Patient patient){
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
        request = HttpRequest.newBuilder()
        .uri(URI.create(aidBoxURL))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Authorization", "Basic ".concat(password)) //Aidbox auth.
        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
        .build();


        return request;

    }

    public HttpRequest updatePatientAidBox(Patient patient){
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

        //Update the patient to AidBox
        request = HttpRequest.newBuilder()
                .uri(URI.create(aidBoxURL.concat(patient.getId())))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic ".concat(password)) //Aidbox auth.
                .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
                .build();


        return request;

    }


}
