import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  templateUrl: './footer.component.html',
  styleUrls: []
})
export class FooterComponent {
  currentYear: number = new Date().getFullYear();
}
