import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../../services/ticket/ticket.service';
import { TourService } from '../../../services/tour/tour.service';
import { PaymentService } from '../../../services/payment/payment.service';
import { Ticket } from '../../../models/ticket.model';
import { Tour } from '../../../models/tour.model';
import { Payment } from '../../../models/payment.model';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  tickets: Ticket[] = [];
  tourOfTicket: { [key: string]: Tour } = {};
  ticketImageUrls: { [key: string]: string } = {};
  displayStatus: { [key: string]: string } = {};

  tours: Tour[] = [];
  tourImageUrls: { [key: string]: string } = {};
  currentPage: number = 1;
  totalPages: number = 1;
  limit: number = 10;

  // Search criteria
  minPrice?: number;
  maxPrice?: number;
  status: string = '';
  startDate?: string;
  endDate?: string;
  sortPrice: string = '';
  sortStatus: string = '';

  constructor(
    private router: Router,
    private ticketService: TicketService,
    private tourService: TourService,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    this.searchTickets();
  }

  searchTickets(page: number = this.currentPage): void {
    this.ticketService.getAllTicketsByUserID(
      this.minPrice,
      this.maxPrice,
      this.sortPrice,
      this.sortStatus,
      this.startDate,
      this.endDate,
      page,
      this.limit
    ).subscribe(
      (result) => {
        this.tickets = result.tickets;
        this.totalPages = result.totalPages;
        this.tours = [];
  
        this.tickets.forEach(ticket => {
          this.fetchTour(ticket);
          this.fetchStatus(ticket);
        });
        console.log("Display status: ", this.displayStatus);
      },
      (error) => {
        console.error('Error fetching tickets:', error);
      }
    );
  }
  
  fetchTour(ticket: Ticket){
    this.tourService.getTourById(ticket.tourID).subscribe(tour => {
      this.tourOfTicket[ticket.id] = tour;
      this.tourService.getTourImage(tour.id).subscribe(blob => {
        const url = window.URL.createObjectURL(blob);
        this.tourImageUrls[tour.id] = url;
      });
    });
  }

  fetchStatus(ticket: Ticket) {
    this.paymentService.getPaymentById(ticket.paymentID).subscribe(payment => {
      if (payment.status === 'UNVERIFIED') {
        this.displayStatus[ticket.id] = 'UNVERIFIED';
      } else if (payment.status === 'VERIFIED' && ticket.status === 'UNUSED') {
        this.displayStatus[ticket.id] = 'VERIFIED';
      } else if (payment.status === 'FAILED') {
        this.displayStatus[ticket.id] = 'FAILED';
      } else if (ticket.status === 'USED') {
        this.displayStatus[ticket.id] = 'USED';
      }
    });
  }

  changePage(page: number) {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.searchTickets(page);
    }
  }

  clearFilters() {
    this.minPrice = undefined;
    this.maxPrice = undefined;
    this.status = '';
    this.startDate = undefined;
    this.endDate = undefined;
    this.sortPrice = '';
    this.searchTickets();
  }

  goToTicketDetails(id: string): void {
    console.log("Details clicked.");
    this.router.navigate(['/ticket'], { queryParams: { id } });
  }
}
