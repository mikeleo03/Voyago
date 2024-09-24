import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-ticket-setup',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule], // Include necessary Angular modules
  templateUrl: './create-ticket-setup.component.html',
  styleUrls: ['./create-ticket-setup.component.css']
})
export class CreateTicketSetupComponent {
  ticketForm: FormGroup;

  constructor(private fb: FormBuilder) {
    // Initialize the form with one customer field
    this.ticketForm = this.fb.group({
      customers: this.fb.array([this.createCustomerGroup()]) // Add one customer by default
    });
  }

  // Helper method to create a customer FormGroup
  createCustomerGroup(): FormGroup {
    return this.fb.group({
      fullName: ['', Validators.required],
      phone: ['', Validators.required]
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

  // Handle form submission
  onSubmit(): void {
    if (this.ticketForm.valid) {
      console.log('Form Submitted:', this.ticketForm.value);
    } else {
      console.log('Form is invalid!');
    }
  }
}