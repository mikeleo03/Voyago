import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  passwordForm!: FormGroup;
  userId: string = '';
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    // Extracting 'id' from the URL as '/change/password/{id}'
    this.userId = this.route.snapshot.paramMap.get('id') ?? '';

    // Initialize the form
    this.passwordForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(12), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).+$/)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator,
      updateOn: 'change' // Trigger form validation on value changes
    });
  }

  // Custom validator for password confirmation
  passwordMatchValidator(formGroup: FormGroup) {
    return formGroup.get('password')!.value === formGroup.get('confirmPassword')!.value
      ? null : { mismatch: true };
  }

  onSubmit() {
    this.isLoading = true;

    // If form is invalid, show errors
    if (this.passwordForm.invalid) {
      this.toastr.error('Please fill out the form correctly.', 'Validation Error');
      this.isLoading = false;
      return;
    }

    // Prepare the request payload
    const newPassRequest = {
      password: this.passwordForm.get('password')!.value
    };

    // Call the service to update the password
    this.userService.updatePassword(this.userId, newPassRequest).subscribe({
      next: () => {
        this.toastr.success('Password updated successfully', 'Success');
        this.router.navigate(['/login']);
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error(err.error.error, 'Error');
        this.isLoading = false;
      }
    });
  }
}
