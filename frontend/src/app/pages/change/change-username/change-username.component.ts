import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-change-username',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-username.component.html',
  styleUrls: ['./change-username.component.css']
})
export class ChangeUsernameComponent {
  mainbg: string = '../assets/img/login.png';
  logo: string = '../assets/icons/logo-color.png';
  isLoading: boolean = false;
  emailForm!: FormGroup;

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    this.isLoading = true;

    if (this.emailForm.valid) {
      const email = this.emailForm.value.email;

      this.userService.getUserByEmail(email).subscribe({
        next: (response) => {
          const subject = '[Voyago] Confirm Your Password Change';
          const htmlContent = `
            <div>
              <img src="https://drive.google.com/uc?export=view&id=1Ih3OppuKRS9hlQJeJeaypfULD7MAjiIl" alt="Voyago Logo" style="width: 100px; height: auto;">
              <h2>📝 Password Change Request</h2>
              <p>Dear <strong>${response.username}</strong>,</p>
              <p>We received a request to change your password.</p>
              <p>Please confirm your request by clicking the button below:</p>
              <a href="http://your-app-url/confirm-change" 
                style="background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block;">
                Confirm Password Change
              </a>
              <p>If you did not request this change, please ignore this email.</p>
              <p>Thank you!</p>
            </div>
          `;

          // Call EmailService to send the email
          this.userService.sendEmail(email, subject, htmlContent).subscribe({
            next: (response: any) => {
              if (response.message === 'HTML Email sent successfully') {
                this.router.navigate(['/change/email/confirmed']);
              } else {
                this.toastr.error('Unexpected response received', 'Error');
              }
              this.isLoading = false;
            },
            error: () => {
              this.toastr.error('Failed to send the email', 'Error');
              this.isLoading = false;
            }
          });
        },
        error: (err) => {
          this.toastr.error(err.error.error, 'Error');
          this.isLoading = false;
        }
      });
    } else {
      this.toastr.error('Please enter a valid email', 'Validation Error');
      this.isLoading = false;
    }
  }
}
