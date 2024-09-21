import { CommonModule, formatDate } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Patient } from '../model/patient';
import { HttpClientModule, HttpClient, HttpHeaders} from '@angular/common/http';
import { patients } from '../../../db-mock/db.json';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { RouterOutlet, RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-patient-form',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule,HttpClientModule,RouterOutlet,RouterModule],
  templateUrl: './patient-form.component.html',
  styleUrl: './patient-form.component.css'
})

export class PatientFormComponent {

  patientForm: FormGroup;
  isFormSubmitted: boolean = false;

  formSubmitButton: boolean = true;
  formUpdateButton: boolean = false;

  patient: Patient;
  patientList: Array<Patient> = patients;

  showSuccessMessage = false;
  showFailMessage = false;
  patientsResult: Patient[];

  apiUrl = "http://localhost:8080/data/upsert/patient";
  apiUrl2 = "http://localhost:8080/search/update/patient";
  response: any;

  //To retrieve the id from the routing parameters (see constructor)
  idPatientFound: string;

    ///////////////////////////////
   /// Patient form constructor //
  ///////////////////////////////
  constructor(private httpClient: HttpClient, private route: ActivatedRoute, private router:Router){
        this.patientForm = new FormGroup({
          name: new FormControl("",[Validators.required, Validators.minLength(4)]),
          dateOfBirth: new FormControl("",[Validators.required]),
          gender: new FormControl("",[Validators.required]),
          phoneNumber: new FormControl("",[Validators.required, Validators.pattern('(([+][(]?[0-9]{1,3}[)]?)|([(]?[0-9]{4}[)]?))\s*[)]?[-\s\.]?[(]?[0-9]{1,3}[)]?([-\s\.]?[0-9]{3})([-\s\.]?[0-9]{3,4})')      ])
        });

        //Prepopulate the form if route is from search patient
        this.route.params.subscribe(params => {
          this.idPatientFound = params['id'];
          let foundPatient = new Patient(params['name'],params['gender'],params['dateOfBirth'],params['phoneNumber']);
          if (foundPatient.name && foundPatient.gender && foundPatient.dateOfBirth && foundPatient.phoneNumber){
            this.formSubmitButton = false;
            this.formUpdateButton = true;

          }
          this.patientForm.patchValue({
            name: foundPatient.name,
            gender: foundPatient.gender,
            dateOfBirth: foundPatient.dateOfBirth,
            phoneNumber: foundPatient.phoneNumber
          });
        });
  }



    ///////////////////////////////
   /// Form onSubmit() fucntion //
  ///////////////////////////////
  onSubmit(){
    const isFormValid = this.patientForm.valid;
    this.isFormSubmitted = true;

    if (isFormValid){
          //AidBox accepts only yyyy-MM-dd date format
          let date = new Date(this.patientForm.controls['dateOfBirth'].value);

          this.patient = new Patient(
            this.patientForm.controls['name'].value,
            this.patientForm.controls['gender'].value,
            formatDate(date,'yyyy-MM-dd',"en-US"),
            this.patientForm.controls['phoneNumber'].value
          )

          const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
          this.httpClient.post<any>(this.apiUrl, this.patient, { headers }).subscribe({
            next: (res: { id: string, name: string, gender: string, dateOfBirth: string, phoneNumber: string }) => {
                console.log('Response from Aidbox:', res); //Log the response from AidBox
                this.patient.id = res.id; // Assign the generated ID to the patient instance
                this.showSuccessMessage = true;
                setTimeout(function(){window.location.href="http://localhost:4200/patient-form";}, 1500);
            },
            error: (err) => {
                this.showFailMessage = true;
                console.error('Error upserting patient:', err);
            }
          });
    }

  }

    ///////////////////////////////
   /// Form onUpdate() function //
  ///////////////////////////////
  onUpdate(){
    const isFormValid = this.patientForm.valid;
    this.isFormSubmitted = true;

    if (isFormValid){
          //AidBox accepts only yyyy-MM-dd date format
          let date = new Date(this.patientForm.controls['dateOfBirth'].value);

          this.patient = new Patient(
            this.patientForm.controls['name'].value,
            this.patientForm.controls['gender'].value,
            formatDate(date,'yyyy-MM-dd',"en-US"),
            this.patientForm.controls['phoneNumber'].value
          )
          this.patient.id = this.idPatientFound;

          const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
          this.httpClient.post<any>(this.apiUrl2, this.patient, { headers }).subscribe({
            next: (res) => {
                console.log('Response from Spring Boot:', res);
                this.showSuccessMessage = true;
                setTimeout(function(){window.location.href="http://localhost:4200/patient-form";}, 1500);
            },
            error: (err) => {
                this.showFailMessage = true;
                console.error('Error updating patient:', err);
            }
          });

    }

  }
}
