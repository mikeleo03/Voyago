import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../services/tour/tour.service';
import { TicketService } from '../../../services/ticket/ticket.service';
import { PaymentService } from '../../../services/payment/payment.service';
import { Payment } from '../../../models/payment.model';
import { Tour } from '../../../models/tour.model';
import { TicketDTO } from '../../../models/ticket.model';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ticket-detail.component.html',
  styleUrl: './ticket-detail.component.css'
})
export class TicketDetailComponent implements OnInit {
  ticketId: string = '';
  ticket: TicketDTO | null = null;
  payment: Payment | null = null;
  tour: Tour | null = null;
  tourImageUrl: string = '';
  paymentImageUrl: string = '';
  buttonLabel: string = '';
  showButton: boolean = true;
  selectedFile: File | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router, 
    private ticketService: TicketService,
    private paymentService: PaymentService,
    private tourService: TourService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.ticketId = params['id'];
    });
    this.loadTicketDetails();
  }

  loadTicketDetails(): void {
    this.ticketService.getTicketById(this.ticketId).subscribe(ticket => {
      this.ticket = ticket;
      this.loadPaymentDetails(ticket.paymentID);
      this.loadTourDetails(ticket.tourID);
    });
  }

  loadPaymentDetails(paymentId: string) {
    this.paymentService.getPaymentById(paymentId).subscribe((payment: Payment) => {
      this.payment = payment;
    });

    this.paymentService.getPaymentImage(paymentId).subscribe(imageBlob => {
      const reader = new FileReader();
      reader.onload = () => {
        this.paymentImageUrl = reader.result as string;
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
}