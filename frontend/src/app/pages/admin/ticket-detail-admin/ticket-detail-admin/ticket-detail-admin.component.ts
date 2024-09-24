import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketService } from '../../../../services/ticket1/ticket1.service';
import { PaymentService } from '../../../../services/payment1/payment1.service';
import { TourService } from '../../../../services/tour/tour.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { Ticket, TicketDTO } from '../../../../models/ticket1.model';
import { Payment } from '../../../../models/payment1.model';
import { Tour } from '../../../../models/tour.model';

@Component({
  selector: 'app-ticket-detail-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ticket-detail-admin.component.html',
  styleUrls: ['./ticket-detail-admin.component.css']
})
export class TicketDetailAdminComponent implements OnInit {
  ticketId: string = '';
  ticket: TicketDTO | null = null;
  payment: Payment | null = null;
  tour: Tour | null = null;
  tourImageUrl: string = '';
  buttonLabel: string = '';
  showButton: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router, 
    private ticketService: TicketService,
    private paymentService: PaymentService,
    private tourService: TourService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (this.authService.getRole() !== 'Admin') {
        this.router.navigate(['/not-found']);
        return;
      }

      this.ticketId = params['id'];
    });
    this.loadTicketDetails();
  }

  loadTicketDetails() {
    this.ticketService.getTicketById(this.ticketId).subscribe(ticket => {
      this.ticket = ticket;
      this.loadPaymentDetails(ticket.paymentID);
      this.loadTourDetails(ticket.tourID);
      this.updateButtonLabel(ticket.status);
    });
  }

  loadPaymentDetails(paymentId: string) {
    this.paymentService.getPaymentById(paymentId).subscribe(payment => {
      this.payment = payment;
    });

    this.paymentService.getPaymentImage(paymentId).subscribe(imageBlob => {
      const reader = new FileReader();
      reader.onload = () => {
        this.tourImageUrl = reader.result as string;
      };
      reader.readAsDataURL(imageBlob);
    });
  }

  loadTourDetails(tourId: string) {
    this.tourService.getTourById(tourId).subscribe(tour => {
      this.tour = tour;
    });

    this.tourService.getTourImage(tourId).subscribe(imageBlob => {
      const reader = new FileReader();
      reader.onload = () => {
        this.tourImageUrl = reader.result as string;
      };
      reader.readAsDataURL(imageBlob);
    });
  }

  updateButtonLabel(status: string) {
    if (status === 'unverified') {
      this.buttonLabel = 'Verify';
      this.showButton = true;
    } else if (status === 'unused') {
      this.buttonLabel = 'Used';
      this.showButton = true;
    } else {
      this.showButton = false;
    }
  }

  verifyPayment() {
    if (this.payment) {
      const newStatus = this.payment.status === 'unverified' ? 'verified' : 'used';
      this.paymentService.changeVerifyStatus(this.payment.id, newStatus).subscribe(() => {
        this.loadTicketDetails();
      });
    }
  }
}
