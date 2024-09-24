import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-create-ticket-payment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-ticket-payment.component.html',
  styleUrls: ['./create-ticket-payment.component.css']
})
export class CreateTicketPaymentComponent implements OnInit, OnDestroy {
  paymentForm: FormGroup;
  isLoading: boolean = false;
  tourImage: string = '/assets/img/empty-img.jpg';
  totalPrice = 400000;
  paymentId: string = '';

  selectedImageFile: File | null = null;
  selectedImageName: string = '';

  // Countdown variables
  hours: string = '00';
  minutes: string = '00';
  seconds: string = '00';
  private countdownInterval: any;

  // Example: Backend-provided time (could be fetched dynamically)
  backendTime: string = "2024-09-24 23:36:35.039819";  // Example provided timestamp

  constructor(private fb: FormBuilder, private route: ActivatedRoute) {
    // Initialize the form with one customer field and date fields
    this.paymentForm = this.fb.group({
      picture: ['']
    });
  }

  ngOnInit(): void {
    // Extracting 'id' from the URL as '/ticket/create/payment/{id}'
    this.paymentId = this.route.snapshot.paramMap.get('id') ?? '';

    // Start the countdown
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  startCountdown(): void {
    // Parse the backend-provided time
    const backendDate = new Date(this.backendTime.replace(" ", "T")); // Convert backend time to valid JS format
    const currentTime = new Date(); // Current time

    // Add 30 minutes to the backend time
    const targetTime = new Date(backendDate.getTime() + 30 * 60 * 1000);

    // Calculate the remaining time from now
    let remainingTime = targetTime.getTime() - currentTime.getTime();

    // If the target time has already passed, don't start the countdown
    if (remainingTime <= 0) {
      this.hours = '00';
      this.minutes = '00';
      this.seconds = '00';
      console.log('The 30-minute window has already passed.');
      return;
    }

    this.countdownInterval = setInterval(() => {
      remainingTime -= 1000;

      // If time is up, stop the countdown
      if (remainingTime <= 0) {
        clearInterval(this.countdownInterval);
        this.hours = '00';
        this.minutes = '00';
        this.seconds = '00';
        console.log('Time is up.');
        return;
      }

      // Calculate hours, minutes, and seconds
      const hours = Math.floor(remainingTime / (1000 * 60 * 60));
      const minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

      // Update the component variables with zero-padded values
      this.hours = this.padTime(hours);
      this.minutes = this.padTime(minutes);
      this.seconds = this.padTime(seconds);

    }, 1000);
  }

  // Helper function to add padding (convert single digit to double digit)
  padTime(value: number): string {
    return value < 10 ? `0${value}` : `${value}`;
  }

  onImageSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImageFile = event.target.files[0];
      if (this.selectedImageFile) {
        this.selectedImageName = this.selectedImageFile.name;
      }
    }
  }

  onSubmit(): void {
    this.isLoading = true;
    if (this.paymentForm.valid) {
      console.log('Form Submitted:', this.paymentForm.value);
    } else {
      console.log('Form is invalid!');
    }
    this.isLoading = false;
  }
}