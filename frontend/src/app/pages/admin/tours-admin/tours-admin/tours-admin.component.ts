import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../../services/tour/tour.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { Tour, TourSave } from '../../../../models/tour.model';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-tours-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tours-admin.component.html',
  styleUrls: ['./tours-admin.component.css']
})
export class ToursAdminComponent implements OnInit {
  tours: Tour[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  limit: number = 10;
  isLoading: boolean = false;

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

  facilities: string[] = [];
  newFacility: string = '';

  isSubmitted = false;

  selectedImage: File | null = null;
  selectedImageName: string = '';
  tourImageUrls: { [key: string]: string } = {};

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tourService: TourService,
    private authService: AuthService,
    private toastrService: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (this.authService.getRole() !== 'Admin') {
        this.router.navigate(['/not-found']);
        return;
      }

      this.location = params['location'] || '';
      this.minPrice = params['minPrice'] ? +params['minPrice'] : undefined;
      this.maxPrice = params['maxPrice'] ? +params['maxPrice'] : undefined;
      this.sortPrice = params['sortPrice'] || '';
      
      this.searchTours();
      this.clearQueryParams();
      
      this.toastrService.overlayContainer = this.toastContainer;
    });
  }

  searchTours(page: number = this.currentPage): void {
    this.tourService.getTours(this.title, this.minPrice, this.maxPrice, this.location, this.sortPrice, page, this.limit).subscribe(
      (result) => {
        this.tours = result.tours;
        this.totalPages = result.totalPages;
        this.tours.forEach(tour => {
          this.tourService.getTourImage(tour.id).subscribe(blob => {
            const url = window.URL.createObjectURL(blob);
            this.tourImageUrls[tour.id] = url;
          });
        });
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

  openModal() {
    this.isModalOpen = true;
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

  isFormValid(): boolean {
    return this.newTour.title.trim() !== '' &&
           this.newTour.location.trim() !== '' &&
           this.newTour.prices > 0 &&
           this.newTour.quota > 0 &&
           this.newTour.detail.trim() !== '';
  }

  saveTour() {
    const imageToUpload = this.selectedImage || undefined;
    this.isSubmitted = true;
    this.isLoading = true;
    
    this.tourService.createTour(this.newTour, imageToUpload).subscribe(
      () => {
        this.searchTours();
        this.resetNewTour();
        this.isModalOpen = false;
        this.toastrService.success('Tour added successfully!');
        this.isLoading = false;
      },
      (error) => {
        console.error('Error saving tour:', error);
        this.toastrService.warning('Error saving tour:', error);
        this.isLoading = false;
      }
    );
}


  resetNewTour() {
    this.newTour = {
      title: '',
      detail: '',
      quota: 0,
      prices: 0,
      location: '',
      status: 'ACTIVE',
    };
    this.selectedImage = null;
    this.selectedImageName = '';
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedImage = file;
      this.selectedImageName = file.name;
    }
  }

  importCSV(file: File) {
    this.tourService.importToursFromCsv(file).subscribe(
      response => {
        console.log('Tours imported successfully:', response);
        this.searchTours();
        this.toastrService.success('Tour imported successfully!');
      },
      error => {
        console.error('Error importing tours:', error);
        this.toastrService.error('Error importing tours:', error);
      }
    );
  }

  goToTourDetails(id: string): void {
    this.router.navigate(['/admin/tour'], { queryParams: { id } });
  }
}
