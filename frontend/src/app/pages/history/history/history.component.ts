import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../../services/ticket/ticket.service';
import { TourService } from '../../../services/tour/tour.service';
import { PaymentService } from '../../../services/payment/payment.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Ticket } from '../../../models/ticket.model';
import { Tour } from '../../../models/tour.model';
import { saveAs } from 'file-saver';

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

  constructor(
    private router: Router,
    private authService: AuthService,
    private ticketService: TicketService,
    private tourService: TourService,
    private paymentService: PaymentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUsername = this.authService.getCurrentUsername();
    if (!currentUsername) {
      this.router.navigate(['/login']);
    };

    this.searchTickets();
  }

  searchTickets(page: number = this.currentPage): void {
    const backendPage = page - 1;  // Adjusting for 0-based index
    this.ticketService.getAllTicketsByUserID(
      this.minPrice,
      this.maxPrice,
      this.startDate,
      this.endDate,
      backendPage,
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
    this.searchTickets();
  }

  goToTicketDetails(id: string): void {
    console.log("Details clicked.");
    this.router.navigate(['/ticket'], { queryParams: { id } });
  }

  exportToExcel() {
    if (this.tickets.length === 0) {
        console.warn('No tickets available for export.');
        return;
    }

    this.ticketService.exportTicketsToExcel(this.tickets).subscribe({
      next: (blob) => {
        const fileName = 'tickets_report.xlsx';
        saveAs(blob, fileName);
      },
      error: (error) => {
        console.error('Failed to export tickets:', error);
      }
    });
  }
}
