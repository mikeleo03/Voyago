import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { LoginRequest } from '../../../models/user.model';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  standalone: true,  // Important! This marks the component as standalone.
  templateUrl: './login.component.html',
  styleUrls: [],
  providers: [
    AuthService
  ], // Services can be provided directly in the component
  imports: [FormsModule, CommonModule] // Import necessary modules directly into the component
})
export class LoginComponent {
  // constants
  mainbg: string = '../assets/img/login.png';
  loginRequest: LoginRequest = { username: '', password: '' };

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(private authService: AuthService, private router: Router, private toastrService: ToastrService) {}

  ngOnInit() {
    this.toastrService.overlayContainer = this.toastContainer;
  }

  login() {
    this.authService.login(this.loginRequest).subscribe({
      next: (response) => {
        if (response.token) {
          this.authService.setToken(response.token);
          this.toastrService.success('Login successful!');
          this.router.navigate(['/']);
        } else {
          this.toastrService.warning('Unexpected error occurred.');
        }
      },
      error: (error) => {
        if (error.status === 401) {
          this.toastrService.error(error.error.error);
        }
      }
    });
  }  
}