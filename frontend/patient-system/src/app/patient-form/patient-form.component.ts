import { CommonModule, formatDate } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule, RouterOutlet } from '@angular/router';
import { Patient } from '../model/patient';
import { environment } from '../environments/environment';

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

  showSuccessMessage = false;
  showFailMessage = false;

  apiUrl = environment.springBootDataInsertPatientURL;
  apiUrl2 = environment.springBootSearchUpdatePatientURL;

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
                this.showFailMessage = false;
                setTimeout(function(){window.location.href=environment.angularLandingPageURL;}, 1500);
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
                this.showFailMessage = false;
                setTimeout(function(){window.location.href=environment.angularLandingPageURL;}, 1500);
            },
            error: (err) => {
                this.showFailMessage = true;
                console.error('Error updating patient:', err);
            }
          });

    }

  }
}
