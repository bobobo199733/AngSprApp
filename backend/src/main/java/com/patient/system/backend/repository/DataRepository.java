package com.patient.system.backend.repository;

import org.springframework.stereotype.Repository;

import com.patient.system.backend.entity.Patient;
import java.util.List;

@Repository
public class DataRepository{
    
    //READ
   public List<Patient> seePatients(){
        return (List <Patient>) seePatients();
    }
            
    //CREATE
    public Patient addPatient(Patient patient){
        return patient;
    }
    
    //UPDATE
    //public Patient updatePatient(Patient patient, Long patientId);

    //DELETE
    //public void deletePatient(Patient patient, Long patientId);


} 
