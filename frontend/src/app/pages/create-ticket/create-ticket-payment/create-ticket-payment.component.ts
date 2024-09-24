import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService, PaymentService, TourService } from '../../../services';

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
  totalPrice = 0;
  paymentId: string = '';
  tourId: string = '';
  tourDetails: any;
  paymentDetails: any;
  isFailed: boolean = false;

  selectedImageFile: File | null = null;
  selectedImageName: string = '';

  // Countdown variables
  hours: string = '00';
  minutes: string = '00';
  seconds: string = '00';
  private countdownInterval: any;

  // Example: Backend-provided time (could be fetched dynamically)
  backendTime: string = "";  // Example provided timestamp

  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private route: ActivatedRoute,
    private tourService: TourService,
    private paymentService: PaymentService,
    private authService: AuthService
  ) {
    // Initialize the form with one customer field and date fields
    this.paymentForm = this.fb.group({
      picture: ['']
    });
  }

  ngOnInit(): void {
    const currentUsername = this.authService.getCurrentUsername();
    // Extracting 'id' from the URL as '/ticket/create/payment/{id}/{tour}'
    this.paymentId = this.route.snapshot.paramMap.get('id') ?? '';
    this.tourId = this.route.snapshot.paramMap.get('tour') ?? '';
    if (currentUsername) {
      if (this.tourId) {
        this.getTourDetails(this.tourId);
        this.getPaymentDetails(this.paymentId);
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
        },
        (error) => {
          console.error('Error fetching tour details:', error);
          this.router.navigate(['/not-found']);
        }
    );
  }

  getPaymentDetails(id: string): void {
    this.paymentService.getPaymentById(id).subscribe(
      (details) => {
        console.log(details);
        this.paymentDetails = details;
        this.totalPrice = details.nominal;
        this.backendTime = details.createdAt.toString(); // Ensure backendTime is populated
        this.isFailed = details.status === "FAILED";
        
        this.isLoading = false;
  
        // Now start the countdown after backendTime is set
        if (this.backendTime) {
          this.startCountdown(); 
        } else {
          console.error('No backend time available to start the countdown');
        }
      },
      (error) => {
        console.error('Error fetching tour details:', error);
        this.router.navigate(['/not-found']);
      }
    );
  }  

  ngOnDestroy(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  startCountdown(): void {
    // Parse the backend-provided time
    const backendDate = new Date(this.backendTime); // Convert backend time to valid JS format
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
    if (this.paymentId) {
      const imageToUpload = this.selectedImageFile || undefined;
      this.paymentService.uploadPaymentEvidence(this.paymentId, imageToUpload).subscribe(() => {
        console.log('Evidence succesfully uploaded');
        this.isLoading = false;
      }, (error) => {
        console.error('Error updating payment:', error);
        this.isLoading = false;
      });
    } else {
      console.error('Payment ID is null, cannot update payment.');
      this.isLoading = false;
    }
  }
}