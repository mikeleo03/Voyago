import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Ticket, TicketDTO } from '../../../../models/ticket1.model';
import { TicketService } from '../../../../services/ticket1/ticket1.service';

@Component({
  selector: 'app-tickets-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tickets-admin.component.html',
  styleUrl: './tickets-admin.component.css'
})
export class TicketsAdminComponent implements OnInit {
  tickets: Ticket[] = [];

  name: string = '';
  minPrice?: number;
  maxPrice?: number;
  sortPrice: string = '';
  sortStatus: string = '';
  startDate: string = '';
  endDate: string = '';

  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 0;

  constructor(private ticketService: TicketService) {}

  ngOnInit() {
    this.fetchTickets();
  }

  fetchTickets() {
    this.ticketService.getAllTickets(
      this.minPrice,
      this.maxPrice,
      this.sortPrice,
      this.startDate,
      this.endDate,
      this.currentPage - 1,
      this.itemsPerPage
    ).subscribe(response => {
      this.tickets = response.tickets;
      this.totalPages = response.totalPages;
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

  viewTicketDetails(ticketId: string) {
    
  }
}