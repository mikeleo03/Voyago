import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-tickets-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tickets-admin.component.html',
  styleUrl: './tickets-admin.component.css'
})
export class TicketsAdminComponent {
  tickets = [
    { id:'a0f27c4c-78bd-11ef-acde-00155ddb498a', date: new Date('2023-09-20'), name: 'User A', status: 'VERIFIED' },
    { id: 'a0f27c4c-78bd-11ef-acde-00155ddb498a', date: new Date('2023-09-19'), name: 'User B', status: 'UNVERIFIED' },
  ];

  name: string = '';
  minPrice?: number;
  maxPrice?: number;
  sortPrice: string = '';
  sortStatus: string = '';
  startDate: Date = new Date();
  endDate: Date = new Date();

  searchName: string = '';
  filterStatus: string = '';
  filteredTickets = this.tickets;
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = Math.ceil(this.tickets.length / this.itemsPerPage);
  
  ngOnInit() {
    this.filterTickets();
  }

  filterTickets() {
    this.filteredTickets = this.tickets
      .filter(ticket => 
        ticket.name.toLowerCase().includes(this.searchName.toLowerCase()) &&
        (this.filterStatus ? ticket.status === this.filterStatus : true)
      )
      .slice((this.currentPage - 1) * this.itemsPerPage, this.currentPage * this.itemsPerPage);
  }

  changePage(page: number) {
    this.currentPage = page;
    this.filterTickets();
  }

  viewTicketDetails(ticketId: string) {
  }
}
