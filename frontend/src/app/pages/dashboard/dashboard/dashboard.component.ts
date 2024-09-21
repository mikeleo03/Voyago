import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: true
})
export class DashboardComponent {
  @Input() userName: string = '';
  @Input() userImgUrl: string = '';
  @Input() userEmail: string = '';
  @Input() userPhone: string = '';
  @Input() historyItems: any[] = [
    {
      title: 'Lorem Ipsum 1',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc aliquet eget mauris vitae laoreet. Ut odio nisi, elementum vitae gravida nec, fermentum scelerisque arcu.',
      date: 'September 12th, 2024',
      price: 'Rp 2.000.000,00',
      imageUrl: '/assets/img/tour1.png'
    },
    {
      title: 'Lorem Ipsum 2',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc aliquet eget mauris vitae laoreet. Ut odio nisi, elementum vitae gravida nec, fermentum scelerisque arcu.',
      date: 'September 12th, 2024',
      price: 'Rp 2.000.000,00',
      imageUrl: '/assets/img/tour2.png'
    }
  ];

  constructor(
    private router: Router,
    private userService: UserService,
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.userName = this.authService.getCurrentUsername();
    this.userService.getUserByUsername(this.userName).subscribe({
      next: (response) => {
        this.userEmail = response.email;
        this.userPhone = response.phone;
        this.userImgUrl = response.picture || "/assets/img/default.png";
      },
      error: () => {
        this.toastr.error("You are unauthenticated, Please login first", 'Unauthenticated');
        this.authService.logout();
        this.router.navigate(['/login']);
      }
    });
  }
}
