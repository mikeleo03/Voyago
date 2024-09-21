import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';  // Import your service
import { ToastContainerDirective, ToastrService } from 'ngx-toastr'; // For toast notifications

@Component({
  selector: 'app-change-username',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-username.component.html',
  styleUrls: ['./change-username.component.css']
})
export class ChangeUsernameComponent {
  // Constants
  mainbg: string = '../assets/img/login.png';
  isLoading: boolean = false;
  emailForm!: FormGroup;

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]], // Email validation
    });
  }

  onSubmit() {
    this.isLoading = true;

    if (this.emailForm.valid) {
      const email = this.emailForm.value.email;

      // Call UserService to check if the email exists
      this.userService.getUserByEmail(email).subscribe({
        next: (response) => {
          // Handle success response
          console.log('User found: ', response);
          this.toastr.success('User found!', 'Success');
          // Always stop loading after the request
          this.isLoading = false;
        },
        error: (err) => {
          // Handle error response
          console.error('Error fetching user:', err);
          this.toastr.error(err.error.error, 'Error');
          // Always stop loading after the request
          this.isLoading = false;
        }
      });
    } else {
      // If form is invalid, show validation message
      this.toastr.error('Please enter a valid email', 'Validation Error');
      this.isLoading = false;
    }
  }
}