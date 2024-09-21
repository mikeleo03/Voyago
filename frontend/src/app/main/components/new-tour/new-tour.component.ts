import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-new-tour',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './new-tour.component.html',
  styleUrl: './new-tour.component.css'
})
export class NewTourComponent {
  isModalOpen = false;
  newTour = {
      title: '',
      location: '',
      price: 0,
      seats: 0,
  };

  openModal() {
      this.isModalOpen = true;
  }

  saveTour() {
      console.log('New Tour:', this.newTour);

      this.newTour = { title: '', location: '', price: 0, seats: 0 };
      this.isModalOpen = false;
  }
}
