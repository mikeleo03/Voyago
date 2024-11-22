import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import CommonModule
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule], // Add CommonModule to imports
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  location: string = '';
  minPrice?: number;
  maxPrice?: number;
  sortPrice: string = '';

  constructor(private router: Router, private authService: AuthService) {}

  searchTours() {
    if (this.authService.getRole() == "Admin"){
      this.router.navigate(['/admin/tours'], {
        queryParams: {
          location: this.location,
          minPrice: this.minPrice,
          maxPrice: this.maxPrice,
          sortPrice: this.sortPrice
        }
      });
    } else{
      this.router.navigate(['/tours'], {
        queryParams: {
          location: this.location,
          minPrice: this.minPrice,
          maxPrice: this.maxPrice,
          sortPrice: this.sortPrice
        }
      });
    }
  }

  // Define dummy data for the tour packages, which can be replaced with backend data later
  tours = [
    { title: 'Papua New Guinea', days: 3, rating: 4.8, imageUrl: '/assets/img/tour1.png' },
    { title: 'Eiffel Tower', days: 3, rating: 4.8, imageUrl: '/assets/img/tour2.png' },
    { title: 'Ropeway Gangtok', days: 3, rating: 4.8, imageUrl: '/assets/img/tour3.png' },
    { title: 'Neemrana Fort-Palace', days: 3, rating: 4.8, imageUrl: '/assets/img/tour4.png' }
  ];

  // Define dummy data for why book icons and reasons
  reasons = [
    {
      title: 'Effortless Booking',
      description: 'Quickly find and book your ideal tour package with just a few taps.',
      icon: '/assets/icons/style1.png',
    },
    {
      title: 'Personalized Experiences',
      description: 'Tailor your trips to your preferences with flexible options and rescheduling.',
      icon: '/assets/icons/style2.png',
    },
    {
      title: 'Seamless Management',
      description: 'Manage bookings, payments, and e-tickets all in one place with ease.',
      icon: '/assets/icons/style3.png',
    },
  ];
}
