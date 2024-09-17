package com.patient.system.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    //Define a HTTP response
    HttpResponse<String> response;

    //The JSONObject will *store* the response == the new patient's data
    //It was needed to get the value of the key "id" 
    JSONObject jsonObject;

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
            //Patient FHIR format JSON body as String
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
