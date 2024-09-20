package com.patient.system.backend.service;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patient.system.backend.control.SearchControl;
import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.repository.AidBoxRepository;
import com.patient.system.backend.repository.DataRepository;

@Service
public class DataService{

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private AidBoxRepository aidBoxRepository;

    HttpClient client = HttpClient.newHttpClient();  
    
    HttpResponse<String> response;

    private static final Logger logger = LoggerFactory.getLogger(SearchControl.class);
    
    //The JSONObject will *store* the response == the new patient's data
    //It was needed to get the value of the key "id" 
    JSONObject jsonObject;

    String jsonPayload; 

    //Create a new patient
    public Patient addPatient(Patient patient){
        try {
            //Patient FHIR format JSON payload
            jsonPayload = String.format( """
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
            response = client.send(aidBoxRepository.insertPatientToAidBox(jsonPayload), HttpResponse.BodyHandlers.ofString());

            //Set the ID of the patient with the one assigned by AidBox
            jsonObject = new JSONObject(response.body());
            patient.setId(jsonObject.getString("id"));

            //Log for AidBox
            logger.info("Patient upserted successfully: {}", patient);

            //If the patient is successfully registered to AidBox
            //Then access Search Service endpoint to index the new patient in OpenSearch
            if(response.statusCode() == 201 || response.statusCode() == 200){
                jsonPayload = String.format( """
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

                //Access via HttpRequest the Search Service endpoint to upsert the same patient in OpenSearch
                HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/search/upsert/patient"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,StandardCharsets.UTF_8))
                .build();
                //Console print outs for OpenSearch
                logger.info("Patient indexed successfully in OpenSearch!");
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            logger.info("Patient upserted successfully: {}", patient);
            return dataRepository.addPatient(patient);

        } catch (Exception e) {
            logger.error("Error occurred while upserting patient: {}", e.getMessage(), e);
            return null;
        } 

    }

    
    //UPDATE
   // public Patient updatePatient(Patient patient, Long patientId){
     //   return dataRepository.updatePatient(patient, patientId);
   // }



}
