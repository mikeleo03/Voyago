import { jwtDecode } from "jwt-decode";
import { Component, OnInit, HostListener } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { AuthService } from '../../../services/auth/auth.service';
import { UserService } from "../../../services/user/user.service";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule, 
    HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective,
    HlmButtonDirective
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  username = '';
  role = '';
  isHome = true;
  isMenuOpen = false;
  showLogout = false;
  isOpen = false;
  isScrolled = false; // Track whether the page is scrolled
  userImageUrl: string = '/assets/img/default.png';

  constructor(private router: Router, private authService: AuthService, private userService: UserService) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    
    if (token != null) {
      const decodedToken: any = jwtDecode(token);
      this.username = decodedToken.sub as string; // Get the 'sub' value for the username
      this.role = this.authService.getRole();

      this.userService.getUserImage(this.username).subscribe(blob => {
        const url = window.URL.createObjectURL(blob);
        this.userImageUrl = url;
      });
    }

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.isHome = event.url == '/';
        this.isScrolled = !this.isHome;
      }
    });
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const scrollPosition = window.scrollY;
    
    // If the user is at the top of the page and it's the home page, set isScrolled to false
    if (scrollPosition <= 50 && this.isHome) {
      this.isScrolled = false;
    } 
    // Otherwise, set isScrolled to true when scroll position is greater than 50
    else if (scrollPosition > 50 || !this.isHome) {
      this.isScrolled = true;
    }
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  handleLogout() {
    this.authService.logout();
    this.router.navigate(['/login']); // Redirect to login page after logout
  }

  // Navigate to profile
  navigateToProfile() {
    this.router.navigate(['/dashboard']);
  }

  // Navigate to tours based on user role
  navigateToTours() {
    if (this.role === 'Admin') {
      this.router.navigate(['/admin/tours']);
    } else {
      this.router.navigate(['/tours']);
    }
  }

  // Navigate to history based on user role
  navigateToHistory() {
    if (this.role === 'Admin') {
      this.router.navigate(['/admin/tickets']);
    } else {
      this.router.navigate(['/history']);
    }
  }

  navigateToCustomers() {
    this.router.navigate(['/admin/customers']);
  }
}