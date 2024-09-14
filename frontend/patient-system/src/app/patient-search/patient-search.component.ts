import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import { RouterOutlet, RouterModule } from '@angular/router';

import { patients } from '../../../db-mock/db.json';
import { Patient } from '../model/patient';

@Component({
  selector: 'app-patient-search',
  standalone: true,
  imports: [FormsModule,CommonModule,RouterModule,RouterOutlet],
  templateUrl: './patient-search.component.html',
  styleUrl: './patient-search.component.css'
})

export class PatientSearchComponent {

  baseURL = "http://localhost:3000/patients";

  patient = new Patient();
  patientList: Array<Patient> = patients;

  patientFound = false;
  patientNotFound = false;

  searchPatient(searchText: string){
    if(searchText != "" && searchText.length > 3){
      for(let i in this.patientList){
        if(this.patientList[i].name.includes(searchText) ){
          this.patient.id = this.patientList[i].id;
          this.patient.name = this.patientList[i].name;
          this.patient.gender = this.patientList[i].gender;
          this.patient.dateOfBirth = this.patientList[i].dateOfBirth;
          this.patient.phoneNumber = this.patientList[i].phoneNumber;
          this.patientFound=true;
          this.patientNotFound = false;
        }else{
          this.patientFound= false;
          this.patientNotFound = true;
        }
      }
    }
    
    
      
  }


  

}
