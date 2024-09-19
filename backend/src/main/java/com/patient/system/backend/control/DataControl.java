package com.patient.system.backend.control;

import org.springframework.web.bind.annotation.RestController;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.service.DataService;

import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/data")
public class DataControl {

    @Autowired 
    private DataService dataService;
    
    private static final Logger logger = LoggerFactory.getLogger(SearchControl.class);

    @PostMapping("/upsert/patient")
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        logger.info("Received request to upsert patient: {}", patient);
        try {
            logger.info("Patient upserted successfully: {}", patient);
            return new ResponseEntity<>(dataService.addPatient(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while upserting patient: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

  
    
}
