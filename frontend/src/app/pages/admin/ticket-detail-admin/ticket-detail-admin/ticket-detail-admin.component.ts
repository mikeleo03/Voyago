import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ticket-detail-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ticket-detail-admin.component.html',
  styleUrl: './ticket-detail-admin.component.css'
})
export class TicketDetailAdminComponent {
  ticketId: string = 'a0f3d975-78bd-11ef-acde-00155ddb498a';
  username: string = 'John Doe';
  tourDetails = {
    description: 'This is a detailed description of the tour.',
  };
  facilities = [
    { name: 'Free WiFi' },
    { name: 'Breakfast Included' },
    { name: 'Swimming Pool Access' },
    { name: '24/7 Customer Support' }]
  paymentDate: string = '2024-09-01';

  tourImageUrl = '';
  tour = {
    title: 'Title',
    detail: 'Detail',
    location: 'Location'
  }

  verifyPayment() {
    console.log('Payment verified for ticket:', this.ticketId);
  }
}
