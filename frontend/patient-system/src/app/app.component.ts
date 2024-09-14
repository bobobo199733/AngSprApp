import { Component } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { PatientFormComponent } from './patient-form/patient-form.component';
import { HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,PatientFormComponent,RouterModule,HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'patient-system';
}
