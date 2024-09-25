import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Ticket } from '../../../../models/ticket.model';
import { Payment } from '../../../../models/payment.model';
import { TicketService } from '../../../../services/ticket/ticket.service';
import { PaymentService } from '../../../../services/payment/payment.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-tickets-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tickets-admin.component.html',
  styleUrl: './tickets-admin.component.css'
})
export class TicketsAdminComponent implements OnInit {
  tickets: Ticket[] = [];
  ticketStatuses: { [key: string]: string } = {};

  name: string = '';
  minPrice?: number;
  maxPrice?: number;
  sortPrice: string = '';
  sortStatus: string = '';
  startDate: string = '';
  endDate: string = '';

  showStatus: string = '';

  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 0;

  constructor(
    private route: ActivatedRoute, 
    private router: Router, 
    private authService: AuthService,
    private ticketService: TicketService,
    private paymentService: PaymentService) {}

  ngOnInit() {
    this.route.queryParams.subscribe(() => {
      if (this.authService.getRole() !== 'Admin') {
        this.router.navigate(['/not-found']);
        return;
      }
      this.fetchTickets();
    });
    this.fetchTickets();
  }

  fetchTickets() {
    this.ticketService.getAllTickets(
      this.minPrice,
      this.maxPrice,
      this.sortPrice,
      this.sortStatus,
      this.startDate,
      this.endDate,
      this.currentPage - 1,
      this.itemsPerPage
    ).subscribe(response => {
      this.tickets = response.tickets;
      this.totalPages = response.totalPages;
      
      this.tickets.forEach(ticket => {
        if (ticket.status === 'USED' || ticket.status === 'FAILED') {
          this.ticketStatuses[ticket.id] = ticket.status;
        } else {
          this.fetchPaymentStatus(ticket.id, ticket.paymentID);
        }
      });
    });
  }

  fetchPaymentStatus(ticketId: string, paymentId: string) {
    this.paymentService.getPaymentById(paymentId).subscribe((payment: Payment) => {
      this.ticketStatuses[ticketId] = payment.status;
    });
  }

  searchTickets() {
    this.currentPage = 1;
    this.fetchTickets();
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.fetchTickets();
    }
  }

  goToTicketDetails(id: string): void {
    this.router.navigate(['/admin/ticket'], { queryParams: { id } });
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