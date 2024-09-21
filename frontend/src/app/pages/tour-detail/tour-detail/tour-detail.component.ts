import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../services/tour/tour.service';

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tour-detail.component.html',
  styleUrl: './tour-detail.component.css'
})
export class TourDetailComponent implements OnInit {
  tourId: string | null = null;
  tourDetails: any;
  loading: boolean = true;

  constructor(private route: ActivatedRoute, private tourService: TourService, private router: Router) {}

  ngOnInit(): void {
      this.route.queryParams.subscribe(params => {
          this.tourId = params['id'];
          if (this.tourId) {
              this.getTourDetails(this.tourId);
          } else {
              this.router.navigate(['/not-found']);
          }
      });
  }

  getTourDetails(id: string): void {
      this.tourService.getTourById(id).subscribe(
          (details) => {
              this.tourDetails = details;
              this.loading = false;
          },
          (error) => {
              console.error('Error fetching tour details:', error);
              this.router.navigate(['/not-found']);
          }
      );
  }

  bookTour() {
      console.log(`Booking tour ID: ${this.tourId}`);
  }

  facilities: string[] = [
    'Wi-Fi',
    'Free Breakfast',
    'Swimming Pool',
    'Parking',
    'Guided Tours',
  ];
}