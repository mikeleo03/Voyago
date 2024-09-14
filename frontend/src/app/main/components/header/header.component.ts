import { jwtDecode } from "jwt-decode";
import { Component, OnInit } from '@angular/core';
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
  ], // Import necessary modules
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  providers: [
    AuthService
  ], // Services can be provided directly in the component
})
export class HeaderComponent implements OnInit {
  username = '';
  isMenuOpen = false;
  showLogout = false;
  isOpen = false;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.username = this.authService.getToken() != null ? jwtDecode(this.authService.getToken() as string).sub as string : "Mr. Lorem Ipsum";
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  handleLogout() {
    this.authService.logout();
    this.router.navigate(['/login']); // Redirect to login page after logout
  }
}
