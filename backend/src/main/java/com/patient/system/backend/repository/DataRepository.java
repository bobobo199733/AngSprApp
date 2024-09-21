package com.patient.system.backend.repository;

import org.springframework.stereotype.Repository;

import com.patient.system.backend.entity.Patient;

@Repository
public class DataRepository{
 
    //CREATE
    public Patient addPatient(Patient patient){
        return patient;
    }

    
} 
