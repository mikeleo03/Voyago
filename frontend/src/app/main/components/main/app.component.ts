import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    HeaderComponent,
    FooterComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: []
})
export class AppComponent implements OnInit {
  showNavAndFooter = true;
  title = 'angular-demo';

  constructor(private router: Router) {}

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        // Hide nav and footer for any URL starting with "/login" or "/signup"
        this.showNavAndFooter = !event.url.startsWith('/login') && !event.url.startsWith('/signup') && !event.url.startsWith('/change');
      }
    });
  }
}