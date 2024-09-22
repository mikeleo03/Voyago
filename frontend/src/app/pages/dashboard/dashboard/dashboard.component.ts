import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { UserUpdateDTO } from '../../../models/user.model';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: true
})
export class DashboardComponent {
  isModalOpen: boolean = false;
  dashboardForm!: FormGroup;
  isLoading: boolean = false;

  // Variables to store user details for profile display
  userName: string = '';
  userEmail: string = '';
  userPhone: string = '';
  userImgUrl: string = '/assets/img/default.png';  // Default image if not available

  @Input() historyItems: any[] = [
    {
      title: 'Lorem Ipsum 1',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc aliquet eget mauris vitae laoreet. Ut odio nisi, elementum vitae gravida nec, fermentum scelerisque arcu.',
      date: 'September 12th, 2024',
      price: 'Rp 2.000.000,00',
      imageUrl: '/assets/img/tour1.png'
    },
    {
      title: 'Lorem Ipsum 2',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc aliquet eget mauris vitae laoreet. Ut odio nisi, elementum vitae gravida nec, fermentum scelerisque arcu.',
      date: 'September 12th, 2024',
      price: 'Rp 2.000.000,00',
      imageUrl: '/assets/img/tour2.png'
    }
  ];
  
  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit() {
    // Initialize the form
    this.dashboardForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      username: [{ value: 'existingUsername', disabled: true }, Validators.required],
      phone: ['', [Validators.required, Validators.pattern(/^(\+62)\d{9,13}$/)]],
      picture: ['']
    });

    this.dashboardForm.get('username')?.disable();

    // Populate form with existing user data
    const currentUsername = this.authService.getCurrentUsername();
    this.userService.getUserByUsername(currentUsername).subscribe({
      next: (response) => {
        // Patch the form with the user's data
        this.dashboardForm.patchValue({
          email: response.email,
          username: response.username,
          phone: response.phone,
          picture: response.picture || '/assets/img/default.png'
        });

        // Set the profile data variables for display
        this.userName = response.username;
        this.userEmail = response.email;
        this.userPhone = response.phone;
        this.userImgUrl = response.picture || '/assets/img/default.png';
      },
      error: () => {
        this.toastr.error('You are unauthenticated, Please login first', 'Unauthenticated');
        this.authService.logout();
        this.router.navigate(['/login']);
      }
    });
  }

  openEditModal(): void {
    this.isModalOpen = true;
  }

  saveUser(): void {
    // Enable the username field just before submitting the form
    this.dashboardForm.get('username')?.enable();

    if (this.dashboardForm.invalid) {
      this.toastr.error('Please fill out all required fields correctly.', 'Validation Error');
      return;
    }
    
    const updatedUser: UserUpdateDTO = this.dashboardForm.value;
    this.isLoading = true;

    this.userService.getUserByUsername(this.authService.getCurrentUsername()).subscribe({
        next: (response) => {
          // Update the user data
          this.userService.updateUser(response.id, updatedUser).subscribe({
            next: (response: any) => {
              this.toastr.success('User data updated successfully', 'Updated Succesfully');
              this.isLoading = false;
              // Update the profile variables after successful save
              this.userName = updatedUser.username;
              this.userEmail = updatedUser.email;
              this.userPhone = updatedUser.phone;
              this.userImgUrl = updatedUser.picture || '/assets/img/default.png';
              this.isModalOpen = false;
            },
            error: () => {
              this.toastr.error('Failed to update user data', 'Error');
              this.isLoading = false;
            }
          });
        },
        error: (err) => {
          this.toastr.error(err.error.error, 'Error');
          this.isLoading = false;
        }
    });

    this.dashboardForm.get('username')?.disable();
  }
}