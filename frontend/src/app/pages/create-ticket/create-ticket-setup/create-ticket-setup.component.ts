import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TicketSaveDTO } from '../../../models/ticket.model';
import { TicketService, AuthService, TourService } from '../../../services';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-create-ticket-setup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], // Include ReactiveFormsModule here
  templateUrl: './create-ticket-setup.component.html',
  styleUrls: ['./create-ticket-setup.component.css']
})
export class CreateTicketSetupComponent implements OnInit {
  ticketForm: FormGroup;
  isLoading: boolean = false;
  ticketPrice = 0;
  tourImage: string = '/assets/img/empty-img.jpg';
  tourId: string = '';
  tourDetails: any;

  constructor(
    private fb: FormBuilder, 
    private ticketService: TicketService,
    private tourService: TourService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // Initialize the form with one customer field and date fields
    this.ticketForm = this.fb.group({
      customers: this.fb.array([this.createCustomerGroup()]),
      startDate: ['', [Validators.required, this.dateValidator]], // Custom date validator
      endDate: ['', [Validators.required, this.dateValidator]]
    }, { validators: this.dateRangeValidator }); // Add custom date range validator
  }

  ngOnInit(): void {
    const currentUsername = this.authService.getCurrentUsername();
    // Extracting 'id' from the URL as '/ticket/create/setup/{id}'
    this.tourId = this.route.snapshot.paramMap.get('id') ?? '';
    if (currentUsername) {
      if (this.tourId) {
        this.getTourDetails(this.tourId);
      } else {
        this.router.navigate(['/not-found']);
      }
    } else {
      this.router.navigate(['/login']);
    };
  }

  getTourDetails(id: string): void {
    this.tourService.getTourById(id).subscribe(
        (details) => {
          this.tourDetails = details;
          this.isLoading = false;
          this.tourService.getTourImage(this.tourId).subscribe(blob => {
            const url = window.URL.createObjectURL(blob);
            this.tourImage = url;
          });
          this.ticketPrice = details.prices;
        },
        (error) => {
          console.error('Error fetching tour details:', error);
          this.router.navigate(['/not-found']);
        }
    );
  }

  // Helper method to create a customer FormGroup
  createCustomerGroup(): FormGroup {
    return this.fb.group({
      fullName: ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/)]],
      phone: ['', [Validators.required, Validators.pattern(/^(\+62)\d{9,13}$/)]]
    });
  }

  // Getter to access the 'customers' FormArray
  get customers(): FormArray {
    return this.ticketForm.get('customers') as FormArray;
  }

  // Method to add a new customer FormGroup
  addCustomer(): void {
    this.customers.push(this.createCustomerGroup());
  }

  // Method to remove a customer FormGroup at a specific index
  removeCustomer(index: number): void {
    if (this.customers.length > 1) {
      this.customers.removeAt(index);
    }
  }

  // Calculate total price based on the number of customers
  get totalPrice(): number {
    return this.customers.length * this.ticketPrice;
  }

  // Custom validator for the date fields (must be today or in the future)
  dateValidator(control: AbstractControl): ValidationErrors | null {
    const inputDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Normalize the time part to avoid issues with comparison

    if (inputDate < today) {
      return { invalidDate: true };
    }
    return null;
  }

  // Custom validator for ensuring endDate is greater than startDate
  dateRangeValidator(group: AbstractControl): ValidationErrors | null {
    const startDate = group.get('startDate')?.value;
    const endDate = group.get('endDate')?.value;

    if (startDate && endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);

      if (end <= start) {
        return { dateRangeInvalid: true }; // Error if endDate is less than or equal to startDate
      }
    }
    return null;
  }

  // Handle form submission
  onSubmit(): void {
    this.isLoading = true;

    const currentUsername = this.authService.getCurrentUsername();
    // Create TicketSaveDTO from form data
    const ticketSaveDTO: TicketSaveDTO = {
      username: currentUsername,
      tourID: this.tourId,
      startDate: this.ticketForm.value.startDate,
      endDate: this.ticketForm.value.endDate,
      ticketDetails: this.customers.value.map((customer: { fullName: string, phone: string }) => ({
        name: customer.fullName,
        phone: customer.phone
      }))
    };

    // Call the service to create the ticket
    this.ticketService.createTicket(ticketSaveDTO).subscribe({
      next: (response) => {
        console.log('Ticket created successfully:', response);
        this.isLoading = false;
        // Handle successful response
        this.router.navigate(['/ticket/create/payment/' + response.paymentID + "/" + this.tourId]);
      },
      error: (error) => {
        console.error('Error creating ticket:', error);
        this.isLoading = false;
      }
    });
  }
}