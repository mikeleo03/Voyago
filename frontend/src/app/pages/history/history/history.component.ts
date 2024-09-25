import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../../services/ticket/ticket.service';
import { TourService } from '../../../services/tour/tour.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Ticket } from '../../../models/ticket.model';
import { Tour } from '../../../models/tour.model';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  tickets: Ticket[] = [];
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
          this.tourService.getTourById(ticket.tourID).subscribe(tour => {
            this.tours.push(tour); 
  
            this.tourService.getTourImage(tour.id).subscribe(blob => {
              const imageUrl = window.URL.createObjectURL(blob);
              this.tourImageUrls[tour.id] = imageUrl;
            });
          });
        });
      },
      (error) => {
        console.error('Error fetching tickets:', error);
      }
    );
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
    this.router.navigate(['/ticket'], { queryParams: { id } });
  }

  loadTickets(): void {
    this.tickets = [];
    this.tours = [];

    this.ticketService.getAllTicketsByUserID().subscribe({
        next: (data) => {
            this.tickets = data.tickets;
            let tourLoadCount = 0;

            this.tickets.forEach((ticket) => {
                this.tourService.getTourById(ticket.tourID).subscribe({
                    next: (tour) => {
                        this.tours.push(tour);

                        this.tourService.getTourImage(tour.id).subscribe(blob => {
                            const imageUrl = window.URL.createObjectURL(blob);
                            this.tourImageUrls[tour.id] = imageUrl;

                            tourLoadCount++;
                            if (tourLoadCount === this.tickets.length) {
                                console.log('All tours loaded successfully');
                            }
                        });
                    },
                    error: (err) => {
                        console.error(`Error loading tour data for tourId ${ticket.tourID}`, err);
                    }
                });
            });
        },
        error: (err) => {
            console.error('Error loading tickets', err);
        }
    });
}
}
