import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClientModule, HttpClient, HttpHeaders} from '@angular/common/http';

import { Patient } from '../model/patient';

@Component({
  selector: 'app-patient-search',
  standalone: true,
  imports: [FormsModule,CommonModule,RouterModule,RouterOutlet,HttpClientModule],
  templateUrl: './patient-search.component.html',
  styleUrl: './patient-search.component.css'
})

export class PatientSearchComponent {

  apiUrl = "http://localhost:8080/search/patient";

  patient = new Patient('null', 'null', 'null', 'null');
  patientList: Array<Patient> = [];

  patientFound = false;
  patientNotFound = false;


    /////////////////////////////////
   /// Patient search constructor //
  /////////////////////////////////
  constructor(private httpClient: HttpClient) {}


  
    ////////////////////////////
   /// Patient search method //
  ////////////////////////////
  searchPatient(searchText: string){
    if(searchText != "" && searchText.length > 3){    
        const headers = new HttpHeaders({'Content-Type': 'application/json','Accept': 'application/json'});
        const encodedName = encodeURIComponent(searchText);
        const url = `${this.apiUrl}?name="${encodedName}"`;
        this.httpClient.get<Patient>(url).subscribe({
          next: (res) => {
            if (res) {
              // Assuming your response structure returns a patient
              this.patient = new Patient(
                res.name,
                res.gender,
                res.dateOfBirth,
                res.phoneNumber
              );
              this.patient.id = res.id; // Assign the ID from the response
              this.patientFound = true;
            } else {
              this.patientNotFound = true;
              setTimeout(function(){window.location.reload();}, 1500);
            }
          },
          error: (err) => {
            console.error('Error searching for patient:', err);
            this.patientNotFound = true;
            this.patientFound = false;
            setTimeout(function(){window.location.reload();}, 1500);
          }
        });
    }
  }
}
