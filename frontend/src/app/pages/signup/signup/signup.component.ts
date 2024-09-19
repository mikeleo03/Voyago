import { Component, ViewChild } from '@angular/core';
import { UserSaveDTO } from '../../../models/user.model';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  templateUrl: './signup.component.html',
  styleUrls: [],
  providers: [
    AuthService, UserService
  ],
  imports: [FormsModule, CommonModule]
})
export class SignupComponent {
  // constants
  isLoading: boolean = false;
  mainbg: string = '../assets/img/login.png';
  signupRequest = { email: '', username: '', password: '', phone: '', confirmPassword: '' };
  userSave: UserSaveDTO = { email: '', username: '', password: '', phone: '' };

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(private authService: AuthService, private userService: UserService, private router: Router, private toastrService: ToastrService) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
    
    this.toastrService.overlayContainer = this.toastContainer;
  }

  signup() {
    this.isLoading = true;
    // Validate the password
    if (this.signupRequest.password!== this.signupRequest.confirmPassword) {
      this.toastrService.error('Passwords do not match.');
      this.isLoading = false;
      return;
    }

    // If not try to fire with API
    this.userService.signup(this.userSave).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.status) {
          this.toastrService.success(response.status);
          this.router.navigate(['/login']);
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
          this.toastrService.error(error.error.error + ", please check all your data." || 'An unexpected error occurred. Please try again.');
        }
      }
    });
  }
}
