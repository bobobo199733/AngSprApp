package com.patient.system.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patient.system.backend.entity.Patient;
import com.patient.system.backend.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/search")
public class SearchControl {

    @Autowired 
    private SearchService searchService;
    
    @PostMapping("/upsert/patient")
    public ResponseEntity<Patient> indexPatient(@RequestBody Patient patient) { 
        try {
            return new ResponseEntity<>(searchService.indexPatient(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/patient")
    public ResponseEntity<Patient> searchPatient(@RequestParam String name) { 
        try {
            return new ResponseEntity<>(searchService.searchPatient(name), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



  
    
}
