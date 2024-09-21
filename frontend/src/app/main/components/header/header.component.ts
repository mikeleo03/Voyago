import { jwtDecode } from "jwt-decode";
import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { AuthService } from '../../../services/auth/auth.service';

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
  isMenuOpen = false;
  showLogout = false;
  isOpen = false;
  isScrolled = false; // Track whether the page is scrolled

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    
    if (token != null) {
      const decodedToken: any = jwtDecode(token);
      this.username = decodedToken.sub as string; // Get the 'sub' value for the username
      this.role = this.authService.getRole();
    } else {
      this.username = "Mr. Lorem Ipsum";
      this.role = "Guest"; // Default role if no token
    }
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const scrollPosition = window.scrollY;
    this.isScrolled = scrollPosition > 50; // Adjust the threshold as needed
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  isLoggedIn(): boolean {
    return this.authService.getToken() != null;
  }

  handleLogout() {
    this.authService.logout();
    this.router.navigate(['/login']); // Redirect to login page after logout
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
      this.router.navigate(['/admin/history']);
    } else {
      this.router.navigate(['/history']);
    }
  }
}