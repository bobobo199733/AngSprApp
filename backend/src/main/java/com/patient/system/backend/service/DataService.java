package com.patient.system.backend.service;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.repository.AidBoxRepository;
import com.patient.system.backend.repository.DataRepository;
import com.patient.system.backend.repository.OpenSearchRepository;

@Service
public class DataService{

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private AidBoxRepository aidBoxRepository;

    HttpClient client = HttpClient.newHttpClient();  
    
    HttpResponse<String> response;

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

            //Console print outs for AidBox
            System.out.println("Patient: " + patient.getId() + " successfully created!");
            System.out.println(HttpStatus.CREATED);

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
                System.out.println("Patient: " + patient.getId() + " successfully indexed!");
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

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


}
