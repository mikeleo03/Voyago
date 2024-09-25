import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../../services/tour/tour.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { FacilityService } from '../../../../services/facility/facility.service';
import { FacilityDTO } from '../../../../models/facility.model';

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
  facilities: FacilityDTO[] = [];
  newFacility: string = '';

  tourImageUrl: string = '';
  selectedImageFile: File | null = null;
  selectedImageName: string | undefined = '';

  isSubmitted = false;

  constructor(
    private route: ActivatedRoute, 
    private tourService: TourService, 
    private facilityService: FacilityService,
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
        this.getFacilitiesByTourId(this.tourId);
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
        this.selectedImageName = details.image;
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

  getFacilitiesByTourId(tourId: string): void {
    this.facilityService.getFacilitiesByTourId(tourId).subscribe(
      (facilities) => {
        this.facilities = facilities;
      },
      (error) => {
        console.error('Error fetching facilities:', error);
      }
    );
  }

  addFacility(): void {
    if (this.newFacility.trim() && this.tourId) {
      const facility: FacilityDTO = { name: this.newFacility.trim(), tourId: this.tourId };
      this.facilityService.createFacility(facility).subscribe(
        () => {
          this.newFacility = '';
          this.getFacilitiesByTourId(this.tourId!);
        },
        (error) => {
          console.error('Error adding facility:', error);
        }
      );
    }
  }
  

  removeFacility(facilityId: string): void {
    this.facilityService.deleteFacility(facilityId).subscribe(
      () => {
        this.facilities = this.facilities.filter(f => f.id !== facilityId);
      },
      (error) => {
        console.error('Error deleting facility:', error);
      }
    );
  }

  openEditModal(): void {
    this.isModalOpen = true;
    this.newTour = { ...this.tourDetails };
  }

  onImageSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImageFile = event.target.files[0];
      if (this.selectedImageFile){
        this.selectedImageName = this.selectedImageFile.name;
      }
    }
  }

  saveTour(): void {
    if (this.tourId) {
      this.isSubmitted = true;
      this.loading = true;
      const imageToUpload = this.selectedImageFile || undefined;
      this.tourService.updateTour(this.tourId as string, this.newTour, imageToUpload).subscribe(() => {
        this.isModalOpen = false;
        this.getTourDetails(this.tourId as string);
        this.loading = false;
      }, (error) => {
        console.error('Error updating tour:', error);
        this.loading = false;
      });
    } else {
      console.error('Tour ID is null, cannot update tour.');
      this.loading = false;
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
