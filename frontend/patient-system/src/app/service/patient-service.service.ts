import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient } from '../model/patient';

@Injectable({
  providedIn: 'root'
})
export class PatientServiceService {

  private apiUrl = 'http://localhost:8080/data/upsert/patient';

  constructor(private http: HttpClient) { }

  upsertPatient(patient: Patient): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(this.apiUrl, patient, { headers: headers });
  }
}
