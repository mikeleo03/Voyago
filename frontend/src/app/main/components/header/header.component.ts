import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';

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
  isMenuOpen = false;
  showLogout = false;
  isOpen = false;
  isScrolled = false; // Track whether the page is scrolled

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.username = "Mr. Lorem Ipsum"; // Replace with auth logic if necessary
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const scrollPosition = window.scrollY;
    this.isScrolled = scrollPosition > 50; // Adjust the threshold as needed
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  handleLogout() {
    this.router.navigate(['/login']); // Redirect to login page after logout
  }
}