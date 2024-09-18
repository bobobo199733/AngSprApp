package com.patient.system.backend.control;

import org.springframework.web.bind.annotation.RestController;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.service.DataService;

import org.springframework.web.bind.annotation.RequestBody;

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
    
    @PostMapping("/upsert/patient")
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        
        try {
            return new ResponseEntity<>(dataService.addPatient(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

  
    
}
