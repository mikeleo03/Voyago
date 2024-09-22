import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../../services/tour/tour.service';
import { AuthService } from '../../../../services/auth/auth.service';

@Component({
  selector: 'app-tour-detail-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tour-detail-admin.component.html',
  styleUrls: ['./tour-detail-admin.component.css']
})
export class TourDetailAdminComponent implements OnInit {
  tourId: string | null = null;
  tourDetails: any;
  loading: boolean = true;

  isModalOpen: boolean = false;
  newTour: any = {};
  facilities: string[] = [
    'Wi-Fi',
    'Free Breakfast',
    'Swimming Pool',
    'Parking',
    'Guided Tours',
  ];
  newFacility: string = '';

  tourImageUrl: string = '';

  isSubmitted = false;

  constructor(
    private route: ActivatedRoute, 
    private tourService: TourService, 
    private router: Router, 
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (this.authService.getRole() !== 'Admin') {
        this.router.navigate(['/not-found']);
        return;
      }

      this.tourId = params['id'];
      if (this.tourId) {
        this.getTourDetails(this.tourId);
      } else {
        this.router.navigate(['/not-found']);
      }
    });
  }

  getTourDetails(id: string): void {
    if (!id) {
      console.error('Tour ID is null or undefined. Cannot fetch tour details.');
      this.router.navigate(['/not-found']);
      return;
    }

    this.tourService.getTourById(id).subscribe(
      (details) => {
        this.tourDetails = details;
        this.loading = false;
        this.tourService.getTourImage(this.tourId as string).subscribe(blob => {
          const url = window.URL.createObjectURL(blob);
          this.tourImageUrl = url;
        });
      },
      (error) => {
        console.error('Error fetching tour details:', error);
        this.router.navigate(['/not-found']);
      }
    );
  }

  openEditModal(): void {
    this.isModalOpen = true;
    this.newTour = { ...this.tourDetails };
  }

  addFacility() {
    if (this.newFacility.trim()) {
      this.facilities.push(this.newFacility.trim());
      this.newFacility = '';
    }
  }

  removeFacility(facility: string) {
    this.facilities = this.facilities.filter(f => f !== facility);
  }

  saveTour(): void {
    if (this.tourId) {
      this.isSubmitted = true;
      this.tourService.updateTour(this.tourId as string, this.newTour).subscribe(() => {
        this.isModalOpen = false;
        this.getTourDetails(this.tourId as string);
      }, (error) => {
        console.error('Error updating tour:', error);
      });
    } else {
      console.error('Tour ID is null, cannot update tour.');
    }
  }

  isFormValid(): boolean {
    return this.newTour.title.trim() !== '' &&
           this.newTour.location.trim() !== '' &&
           this.newTour.prices > 0 &&
           this.newTour.quota > 0 &&
           this.newTour.detail.trim() !== '';
  }
}
