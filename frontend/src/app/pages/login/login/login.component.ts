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
  isLoading: boolean = false;
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
    this.isLoading = true;
    this.authService.login(this.loginRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.token) {
          this.authService.setToken(response.token);
          this.toastrService.success('Login successful!');
          this.router.navigate(['/']);
        } else {
          this.toastrService.warning('Unexpected error occurred.');
        }
      },
      error: (error) => {
        this.isLoading = false;
        // Check if error.status exists and act accordingly
        if (error.status === 400) {
          // Handle 400 Bad Request with multiple errors
          if (error.error?.errors && Array.isArray(error.error.errors)) {
            // Display each error as its own toast
            error.error.errors.forEach((errMessage: string) => {
              this.toastrService.error(errMessage, 'Validation Error');
            });
          } else if (error.error?.error) {
            // Handle single error message
            this.toastrService.error(error.error.error, 'Validation Error');
          } else {
            // Handle unexpected format of 400 errors
            this.toastrService.error("Please check your username or password.", 'Bad Request');
          }
        } else if (error.status === 401) {
          // Handle 401 Unauthorized errors
          this.toastrService.error("Please check your username or password.", 'Bad Request');
        } else {
          // Handle any other unexpected errors
          this.toastrService.error("Please check your username or password.", 'Bad Request');
        }
      }
    });
  }    
}