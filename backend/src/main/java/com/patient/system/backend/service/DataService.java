package com.patient.system.backend.service;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.patient.system.backend.repository.OpenSearchRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.repository.AidBoxRepository;

@Service
public class DataService{

    @Autowired
    private AidBoxRepository aidBoxRepository;

    @Autowired
    private OpenSearchRepository openSearchRepository;
    @Value("${bootUrlBase}")
    private String bootUrlBase;
    HttpClient client = HttpClient.newHttpClient();

    HttpResponse<String> response;

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    //The JSONObject will *store* the response == the new patient's data
    //It was needed to get the value of the key "id" 
    JSONObject jsonObject;

    String jsonPayload;

    //Create a new patient
    public Patient addPatient(Patient patient){
        try {
            //Register the patient to AidBox
            response = client.send(aidBoxRepository.insertPatientToAidBox(patient), HttpResponse.BodyHandlers.ofString());

            //Set the ID of the patient with the one assigned by AidBox
            jsonObject = new JSONObject(response.body());
            patient.setId(jsonObject.getString("id"));

            //Log for AidBox
            logger.info("Patient upserted successfully: {}", patient);

            //If the patient is successfully registered to AidBox
            //Then access Search Service endpoint to index the new patient in OpenSearch
            if(response.statusCode() == 201 || response.statusCode() == 200){
                response = client.send(openSearchRepository.indexPatientToOpenSearch(patient), HttpResponse.BodyHandlers.ofString());

                //Console print-outs for OpenSearch
                System.out.println("Patient indexed successfully in OpenSearch!");
                logger.info("Patient indexed successfully in OpenSearch!");

            }

            System.out.println("Patient upserted successfully!");
            logger.info("Patient upserted successfully: {}", patient);

            return patient;

        } catch (Exception e) {
            System.out.println("Error in request to AidBox!");
            System.out.println(e);
            System.out.println(e.getMessage());
            logger.error("Error occurred while upserting patient: {}", e.getMessage(), e);

            return null;
        }

    }
}