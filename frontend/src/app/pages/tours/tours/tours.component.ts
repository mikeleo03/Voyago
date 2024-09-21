import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../services/tour/tour.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Tour } from '../../../models/tour.model';

@Component({
  selector: 'app-tours',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tours.component.html',
  styleUrls: ['./tours.component.css']
})
export class ToursComponent implements OnInit {
  tours: Tour[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  limit: number = 10; 

  title: string = '';
  minPrice?: number;
  maxPrice?: number;
  location: string = '';
  sortPrice: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tourService: TourService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.location = params['location'] || '';
      this.minPrice = params['minPrice'] ? +params['minPrice'] : undefined;
      this.maxPrice = params['maxPrice'] ? +params['maxPrice'] : undefined;
      this.sortPrice = params['sortPrice'] || '';
      
      this.searchTours();
      this.clearQueryParams();
    });
  }

  searchTours(page: number = this.currentPage): void {
    this.tourService.getTours(this.title, this.minPrice, this.maxPrice, this.location, this.sortPrice, page, this.limit).subscribe(
      (result) => {
        this.tours = result.tours;
        this.totalPages = result.totalPages;
      },
      (error) => {
        console.error('Error fetching tours:', error);
      }
    );
  }

  changePage(page: number) {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.searchTours(page);
    }
  }

  clearQueryParams() {
    this.router.navigate([], {
      queryParams: {},
      replaceUrl: true
    });
  }

  goToTourDetails(id: string): void {
    this.router.navigate(['/tour'], { queryParams: { id } });
  }  
}
