import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { UserUpdateDTO } from '../../../models/user.model';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Ticket } from '../../../models/ticket.model';
import { TicketService, TourService } from '../../../services';
import { Tour } from '../../../models/tour.model';

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
  role: string = "";

  selectedImageFile: File | null = null;
  selectedImageName: string = '/assets/img/default.png';

  // Variables to store user details for profile display
  userName: string = '';
  userEmail: string = '';
  userPhone: string = '';
  tickets: Ticket[] = [];
  tours: any[] = [];
  tourImageUrls: { [key: string]: string } = {};

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
    private ticketService: TicketService,
    private tourService: TourService,
    private router: Router
  ) {}

  formatDate(dateArray: number[]): string {
    const [year, month, day] = dateArray;
  
    // Create a new Date object from the array
    const date = new Date(year, month - 1, day); // month is zero-indexed in JavaScript
  
    // Define options for formatting
    const options: Intl.DateTimeFormatOptions = { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    };
  
    // Format the date
    return date.toLocaleDateString('en-US', options);
  }

  searchTickets(page: number = 1, size: number = 3): void {
    this.ticketService.getAllTicketsByUserID(
      undefined, // minPrice
      undefined, // maxPrice
      undefined, // sortPrice
      undefined, // sortStatus
      undefined, // startDate
      undefined, // endDate
      page,      // page number
      size       // size per page
    ).subscribe(
      (result) => {
        this.tickets = result.tickets;
        this.tours = [];
  
        this.tickets.forEach(ticket => {
          this.tourService.getTourById(ticket.tourID).subscribe(tour => {
            // Add startDate and endDate from the ticket to the tour object
            const tourWithDates = { ...tour, startDate: this.formatDate(ticket.startDate), endDate: this.formatDate(ticket.endDate) };

            // Push the modified tour object with startDate and endDate
            this.tours.push(tourWithDates); 
  
            this.tourService.getTourImage(tour.id).subscribe(blob => {
              const imageUrl = window.URL.createObjectURL(blob);
              this.tourImageUrls[tour.id] = imageUrl;
            });
          });
        });
      },
      (error) => {
        console.error('Error fetching tickets:', error);
      }
    );
  }

  ngOnInit() {
    this.role = this.authService.getRole();
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
        this.userService.getUserImage(this.userName).subscribe(blob => {
          const url = window.URL.createObjectURL(blob);
          this.selectedImageName = url;
        });
      },
      error: () => {
        this.toastr.error('You are unauthenticated, Please login first', 'Unauthenticated');
        this.authService.logout();
        this.router.navigate(['/login']);
      }
    });

    this.searchTickets();
  }

  openEditModal(): void {
    this.isModalOpen = true;
  }

  onImageSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImageFile = event.target.files[0];
      if (this.selectedImageFile){
        this.selectedImageName = this.selectedImageFile.name;
      }
    }
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
          this.userService.updateUser(response.id, updatedUser, this.selectedImageFile as File).subscribe({
            next: (response: any) => {
              this.toastr.success('User data updated successfully', 'Updated Succesfully');
              this.isLoading = false;
              // Update the profile variables after successful save
              this.userName = updatedUser.username;
              this.userEmail = updatedUser.email;
              this.userPhone = updatedUser.phone;
              this.selectedImageName = updatedUser.picture || '/assets/img/default.png';
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