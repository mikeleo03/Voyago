import { Component, OnInit, ViewChild } from '@angular/core';
import { UserService } from '../../../../services/user/user.service';
import { User } from '../../../../models/user.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../services/auth/auth.service';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-customers-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers-admin.component.html',
  styleUrls: ['./customers-admin.component.css']
})
export class CustomersAdminComponent implements OnInit {
  users: User[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  limit: number = 10;

  isModalOpen = false;

  name: string = '';

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private toastrService: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (this.authService.getRole() !== 'Admin') {
        this.router.navigate(['/not-found']);
        return;
      }

      this.name = params['name'] || '';
      
      this.searchUsers();
      this.clearQueryParams();
      
      this.toastrService.overlayContainer = this.toastContainer;
    });
  }

  searchUsers(page: number = this.currentPage): void {
    this.userService.getUsers(this.name, page, this.limit).subscribe(
      (result) => {
        this.users = result.users;
        this.totalPages = result.totalPages;
        console.log('Fetched tours:', this.users); // For debugging
      },
      (error) => {
        console.error('Error fetching users:', error);
      }
    );
  }

  changePage(page: number) {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.searchUsers(page);
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
}