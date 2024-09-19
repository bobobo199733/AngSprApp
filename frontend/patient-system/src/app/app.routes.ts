import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PatientFormComponent } from './patient-form/patient-form.component';
import { PatientSearchComponent } from './patient-search/patient-search.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';


export const routes: Routes = [
    {path: '', redirectTo: '/patient-form', pathMatch: 'full'},
    {path: 'patient-form', component: PatientFormComponent},
    {path: 'patient-search', component: PatientSearchComponent},
    {path: 'patient-form/:id/:name/:gender/:dateOfBirth/:phoneNumber', component: PatientFormComponent},

];

@NgModule({
    imports: [RouterModule.forRoot(routes),BrowserAnimationsModule],
    exports: [RouterModule]
})

export class AppRoutingModule { }