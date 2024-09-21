import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../../services/tour/tour.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { Tour, TourSave } from '../../../../models/tour.model';

@Component({
  selector: 'app-tours-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tours-admin.component.html',
  styleUrl: './tours-admin.component.css'
})
export class ToursAdminComponent implements OnInit {
  tours: Tour[] = [];

  isModalOpen = false;
  newTour: TourSave = {
    title: '',
    detail: '',
    quota: 0,
    prices: 0,
    location: '',
    image: '',
    status: 'ACTIVE',
  };

  title: string = '';
  minPrice?: number;
  maxPrice?: number;
  location: string = '';
  sortPrice: string = '';

  constructor(private route: ActivatedRoute, private router: Router, private tourService: TourService, private authService: AuthService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.location = params['location'] || '';
      this.minPrice = params['minPrice'] ? +params['minPrice'] : undefined;
      this.maxPrice = params['maxPrice'] ? +params['maxPrice'] : undefined;
      this.sortPrice = params['sortPrice'] || '';
      
      this.searchTours();
      this.clearQueryParams();
      console.log(this.authService.getRole());
    });
  }

  openModal() {
    this.isModalOpen = true;
  }

  searchTours() {
    this.tourService.getTours(this.title, this.minPrice, this.maxPrice, this.location, this.sortPrice).subscribe(
      (result) => {
        this.tours = result;
      },
      (error) => {
        console.error('Error fetching tours:', error);
      }
    );
  }

  clearQueryParams() {
    this.router.navigate([], {
      queryParams: {},
      replaceUrl: true
    });
  }

  saveTour() {
    console.log('New Tour:', this.newTour);
  
    this.tourService.createTour(this.newTour).subscribe(
      () => {
        this.searchTours();
  
        this.newTour = {
          title: '',
          detail: '',
          quota: 0,
          prices: 0,
          location: '',
          image: '',
          status: 'ACTIVE',
        };
  
        this.isModalOpen = false;
      },
      (error) => {
        console.error('Error saving tour:', error);
      }
    );
  }  
  
  importCSV() {
    console.log('Import CSV clicked');
  }
}