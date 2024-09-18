package com.patient.system.backend.repository;

import org.springframework.stereotype.Repository;

import com.patient.system.backend.entity.Patient;

@Repository
public class SearchRepository {
    
    //CREATE
    public Patient indexPatient(Patient patient){
        return patient;
    }

    public Patient searchPatient(String name){
        return new Patient(); 
    }


}
