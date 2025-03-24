import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { PagedResponse } from '../../../models/common.types';
import { AuthService } from 'src/app/services/auth.service';
import { AUTHORITY } from 'src/app/models/role.model';
import { switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule ]
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  loading = false;
  selectedStatus: string | null = null;
  error: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 15; // Update to match backend default size
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  constructor(private userService: UserService
    ,    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  onStatusChange() {
    this.currentPage = 0; // Reset to first page when filter changes
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.error = null;

    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          if (this.selectedStatus) {
            return this.userService.getUsersByStatus(
              this.selectedStatus,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          } else {
            return this.userService.getUsers(
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          }
        } else {
          const branchId = currentUser.branch.id;
          if (this.selectedStatus) {
            return this.userService.getUsersByBranchAndStatus(
              this.selectedStatus,
              branchId,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          } else {
            return this.userService.getUsersByBranch(
              branchId,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          }
        }
      })
    ).subscribe({
      next: (response: PagedResponse<User>) => {
        this.users = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (error: any) => {  
        console.error('Error loading users:', error);
        this.error = 'Failed to load users';
        this.loading = false;
      }
    });
  }


  onPageChange(page: number) {
    this.currentPage = page;
    this.loadUsers();
  }

  get pages(): number[] {
    const pages = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  getUserStatusText(status: string): string {
    return status; // Simply return the status as it's already in the correct format
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'terminated':
        return 'bg-danger';
      case 'retired':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }
}
