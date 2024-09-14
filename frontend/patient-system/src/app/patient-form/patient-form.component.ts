import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Patient } from '../model/patient';
import { HttpClientModule, HttpClient} from '@angular/common/http';
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
  
  patient = new Patient();
  patientList: Array<Patient> = patients;

  showSuccessMessage = false;
  showFailMessage = false;
  patientsResult: Patient[];

  baseURL = "http://localhost:3000/patients";
  
  foundPatientId: string;

  getPacientById(pacientID : string){
    return this.httpClient.get<Patient>(this.baseURL+"/"+this.foundPatientId);
  }

  updatePatient(pacientID: string, updatedData:any): Observable<Patient> {
    return this.httpClient.put<Patient>(this.baseURL+"/"+this.foundPatientId, this.patient);
  }

  ///////////////////////////////
  /// Patient form constructor //
  ///////////////////////////////
  constructor(private httpClient: HttpClient, private route: ActivatedRoute, private router:Router){
    this.patientForm = new FormGroup({
      name: new FormControl("",[Validators.required, Validators.minLength(4)]), 
      dateOfBirth: new FormControl("",[Validators.required]), 
      gender: new FormControl("",[Validators.required]),
      phoneNumber: new FormControl("",[Validators.required, Validators.pattern('(([+][(]?[0-9]{1,3}[)]?)|([(]?[0-9]{4}[)]?))\s*[)]?[-\s\.]?[(]?[0-9]{1,3}[)]?([-\s\.]?[0-9]{3})([-\s\.]?[0-9]{3,4})')      ])
    })

    //for pre-populated field
    this.route.params.subscribe(params => {this.foundPatientId = params['pacientId'];  console.log('Test ID:', this.foundPatientId)});
    if(this.foundPatientId !== ""){
      this.getPacientById(this.foundPatientId).subscribe(foundPatient=>{
        this.patient = foundPatient;
        console.log(this.patient);
          this.patientForm.patchValue({
            id: this.patient.id,
            name: this.patient.name,
            dateOfBirth: this.patient.dateOfBirth,
            gender: this.patient.gender,
            phoneNumber: this.patient.phoneNumber
          }
        )
      });
    } 
  }


    ///////////////////////////////
   /// Form onSubmit() fucntion //
  ///////////////////////////////
  onSubmit(){
    const isFormValid = this.patientForm.valid;
    this.isFormSubmitted = true;

    
    if (isFormValid){
      //debugger;
      //this.httpClient.get(this.baseURL).subscribe((data)=>{ this.data = this.patient}) 
     
      //  for(let i in this.patientList){
      //    if(this.patientList[i].firstName !== this.patientForm.controls['firstName'].value &&
      //      this.patientList[i].lastName !== this.patientForm.controls['lastName'].value &&
      //      this.patientList[i].dateOfBirth !== this.patientForm.controls['dateOfBirth'].value &&
      //      this.patientList[i].gender !== this.patientForm.controls['gender'].value &&
      //      this.patientList[i].phoneNumber !== this.patientForm.controls['phoneNumber'].value)
      //      {
      //          this.patient.firstName = this.patientForm.controls['firstName'].value;
      //          this.patient.lastName = this.patientForm.controls['lastName'].value;
      //          this.patient.dateOfBirth = this.patientForm.controls['dateOfBirth'].value;
      //          this.patient.gender = this.patientForm.controls['gender'].value;
      //          this.patient.phoneNumber = this.patientForm.controls['phoneNumber'].value;
      //          this.showSuccessMessage = true;
      //          this.httpClient.post(this.baseURL,this.patient).subscribe();  
      //          setTimeout(function(){window.location.reload();}, 1500);
      //    } else {
      //      this.showFailMessage = true;
      //      setTimeout(function(){window.location.reload();}, 1500);
      //     
      //    }      
      //  }
      
      
      //update part logic bricked
      //if(this.foundPatientId === ""){
      //  this.updatePatient(this.foundPatientId, this.patient).subscribe(patientUpdated=>{
      //    this.patient.id = patientUpdated.id;
      //    this.patient.name = patientUpdated.name;
      //    this.patient.dateOfBirth = patientUpdated.dateOfBirth;
      //    this.patient.gender = patientUpdated.gender;
      //    this.patient.phoneNumber = patientUpdated.phoneNumber;})
      //  } else {

          this.patient.name = this.patientForm.controls['name'].value;
          this.patient.dateOfBirth = this.patientForm.controls['dateOfBirth'].value;
          this.patient.gender = this.patientForm.controls['gender'].value;
          this.patient.phoneNumber = this.patientForm.controls['phoneNumber'].value;
          this.showSuccessMessage = true;
          this.httpClient.post(this.baseURL,this.patient).subscribe();  
          this.showSuccessMessage = true;
          
          setTimeout(()=>{this.router.navigate(['/patient-form']), 1500});

      
    }
 
  }
}
