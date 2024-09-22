import { Component, OnInit, ViewChild } from '@angular/core';
import { UserService } from '../../../../services/user/user.service';
import { User } from '../../../../models/user.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../services/auth/auth.service';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-customers-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers-admin.component.html',
  styleUrls: ['./customers-admin.component.css']
})
export class CustomersAdminComponent implements OnInit {
  currentUsername = '';
  users: User[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  limit: number = 10;

  isModalOpen = false;
  userImageUrls: { [key: string]: string } = {};

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

    const token = this.authService.getToken();
    
    if (token != null) {
      const decodedToken: any = jwtDecode(token);
      this.currentUsername = decodedToken.sub as string; // Get the 'sub' value for the username
    } else {
      this.currentUsername = "Mr. Lorem Ipsum";
    }
  }

  searchUsers(page: number = this.currentPage): void {
    this.userService.getUsers(this.name, page, this.limit).subscribe(
      (result) => {
        this.users = result.users;
        this.totalPages = result.totalPages;
        this.users.forEach(user => {
          this.userService.getUserImage(user.username).subscribe(blob => {
            const url = window.URL.createObjectURL(blob);
            this.userImageUrls[user.id] = url;
          });
        });
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

  toggleUserStatus(userId: string, event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const isChecked = inputElement.checked;
    const newStatus = isChecked ? 'ACTIVE' : 'INACTIVE';
  
    // Update status in the backend
    this.userService.updateUserStatus(userId, newStatus).subscribe(
      (response) => {
        // Successfully updated status, now update the local user array
        const user = this.users.find(u => u.id === userId);
        if (user) {
          user.status = newStatus; // Update local status
        }
        console.log('User status updated:', response);
      },
      (error) => {
        console.error('Failed to update user status:', error);
        // Optionally revert the checkbox if the update failed
        inputElement.checked = !isChecked; // Revert the checkbox
      }
    );
  }  
}