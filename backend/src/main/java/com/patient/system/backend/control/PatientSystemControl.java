package com.patient.system.backend.control;

import org.springframework.web.bind.annotation.RestController;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.service.DataService;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/patientSystem")
public class PatientSystemControl {

    @Autowired 
    private DataService dataService;
    
    @PostMapping("/getAllPatients")
    public List<Patient> seePatients(@RequestBody Patient patient) {
        try {
            return (List<Patient>) dataService.seePatients();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/upsert/patient")
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        try {
            return new ResponseEntity<>(dataService.addPatient(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

  
    
}
