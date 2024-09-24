import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-ticket-setup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], // Include ReactiveFormsModule here
  templateUrl: './create-ticket-setup.component.html',
  styleUrls: ['./create-ticket-setup.component.css']
})
export class CreateTicketSetupComponent {
  ticketForm: FormGroup;
  isLoading: boolean = false;
  ticketPrice = 200000; // Constant for price per ticket (e.g., Rp 200.000)
  tourImage: string = '/assets/img/empty-img.jpg';

  constructor(private fb: FormBuilder) {
    // Initialize the form with one customer field and date fields
    this.ticketForm = this.fb.group({
      customers: this.fb.array([this.createCustomerGroup()]),
      startDate: ['', [Validators.required, this.dateValidator]], // Custom date validator
      endDate: ['', [Validators.required, this.dateValidator]]
    }, { validators: this.dateRangeValidator }); // Add custom date range validator
  }

  // Helper method to create a customer FormGroup
  createCustomerGroup(): FormGroup {
    return this.fb.group({
      fullName: ['', Validators.required],
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
    if (this.ticketForm.valid) {
      console.log('Form Submitted:', this.ticketForm.value);
    } else {
      console.log('Form is invalid!');
    }
    this.isLoading = false;
  }
}