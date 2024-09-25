import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketService } from '../../../../services/ticket/ticket.service';
import { PaymentService } from '../../../../services/payment/payment.service';
import { TourService } from '../../../../services/tour/tour.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { TicketDTO } from '../../../../models/ticket.model';
import { Payment } from '../../../../models/payment.model';
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
  paymentImageUrl: string = '';
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
    });
  }

  loadPaymentDetails(paymentId: string) {
    this.paymentService.getPaymentById(paymentId).subscribe((payment: Payment) => {
      this.payment = payment;
      this.paymentService.getPaymentImage(paymentId).subscribe(imageBlob => {
        const reader = new FileReader();
        reader.onload = () => {
          this.paymentImageUrl = reader.result as string;
        };
        reader.readAsDataURL(imageBlob);
      });
      this.updateButtonLabel();
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

  updateButtonLabel() {
    if (this.payment?.status === 'UNVERIFIED') {
      this.buttonLabel = 'Verify';
      this.showButton = true;
    } else if (this.payment?.status === 'VERIFIED' && this.ticket?.status === 'UNUSED') {
      this.buttonLabel = 'Use';
      this.showButton = true;
    } else {
      this.showButton = false;
    }
  }

  actionButton() {
    if (this.payment) {
      if (this.payment?.status === 'UNVERIFIED') {
        this.paymentService.changeVerifyStatus(this.payment.id, 'VERIFIED').subscribe(() => {
          this.loadTicketDetails();
        });
      } else if (this.ticket?.status === 'UNUSED') {
        this.ticketService.updateTicketStatus(this.ticketId, 'USED').subscribe(() => {
          this.loadTicketDetails();
        });
      } else {
        this.showButton = false;
      }
    }
  }
}
