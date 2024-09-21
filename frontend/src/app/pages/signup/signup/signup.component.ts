import { Component, ViewChild } from '@angular/core';
import { UserSaveDTO } from '../../../models/user.model';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
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
  imports: [ReactiveFormsModule, CommonModule]
})
export class SignupComponent {
  // constants
  isLoading: boolean = false;
  mainbg: string = '../assets/img/signup.png';

  signupForm!: FormGroup;

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private toastrService: ToastrService,
    private fb: FormBuilder // Inject FormBuilder
  ) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }

    // Initialize the form with validators and updateOn
    this.signupForm = this.fb.group({
      username: ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\+62\d{9,13}$/)]],
      password: ['', [Validators.required, Validators.minLength(12), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).+$/)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator,
      updateOn: 'change' // Trigger form validation on value changes
    });
    
    this.toastrService.overlayContainer = this.toastContainer;
  }

  // Custom validator for password confirmation
  passwordMatchValidator(formGroup: FormGroup) {
    return formGroup.get('password')!.value === formGroup.get('confirmPassword')!.value
      ? null : { mismatch: true };
  }

  signup() {
    this.isLoading = true;

    // If form is invalid, show errors
    if (this.signupForm.invalid) {
      this.toastrService.error('Please fill out the form correctly.');
      this.isLoading = false;
      return;
    }

    // Prepare the request payload
    const signupRequest: UserSaveDTO = {
      username: this.signupForm.get('username')!.value,
      email: this.signupForm.get('email')!.value,
      phone: this.signupForm.get('phone')!.value,
      password: this.signupForm.get('password')!.value
    };

    this.userService.signup(signupRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.status) {
          this.toastrService.success('Account created successfully!');
          this.router.navigate(['/login']);
        } else {
          this.toastrService.warning('Unexpected error occurred.');
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.handleSignupErrors(error);
      }
    });
  }

  private handleSignupErrors(error: any) {
    if (error.status === 400) {
      const errors = error.error?.errors;
      if (Array.isArray(errors)) {
        errors.forEach((errMessage: string) => this.toastrService.error(errMessage, 'Validation Error'));
      } else {
        this.toastrService.error('Please check your inputs.', 'Bad Request');
      }
    } else if (error.status === 401) {
      this.toastrService.error('Unauthorized: Invalid credentials.');
    } else {
      this.toastrService.error('An unexpected error occurred.', 'Error');
    }
  }
}
