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
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
    
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
        // Check if error.status exists and act accordingly
        if (error.status === 400) {
          // Handle 400 Bad Request with multiple errors
          if (error.error?.errors && Array.isArray(error.error.errors)) {
            // Display each error as its own toast
            error.error.errors.forEach((errMessage: string) => {
              this.toastrService.error(errMessage);
            });
          } else if (error.error?.error) {
            // Handle single error message
            this.toastrService.error(error.error.error);
          } else {
            // Handle unexpected format of 400 errors
            this.toastrService.error('Bad Request: Please check your inputs.');
          }
        } else if (error.status === 401) {
          // Handle 401 Unauthorized errors
          this.toastrService.error('Unauthorized: Invalid username or password.');
        } else {
          // Handle any other unexpected errors
          this.toastrService.error(error.error.error + ", please check your username or password." || 'An unexpected error occurred. Please try again.');
        }
      }
    });
  }    
}