import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import {  CurrentUser, User } from '../../../models/user.model';
import { PagedResponse } from '../../../models/common.types';
import { AuthService } from 'src/app/services/auth.service';
import { AUTHORITY } from 'src/app/models/role.model';
import { of, switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { BranchService } from 'src/app/services/branch.service';
import { Branch } from 'src/app/models/branch.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule ]
})
export class UserListComponent implements OnInit {
  searchTerm: string = '';
filteredUsers: User[] = [];
  branches: Branch[] = [];
  readonly AUTHORITY = AUTHORITY;
  selectedBranch: Branch | null = null;
  // Update the property definition
currentUser: CurrentUser | null = null;
 
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
    ,    private authService: AuthService,
    private branchService: BranchService
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.loadCurrentUser();
  }
  onSearchChange() {
    // If no search term, reset the filtered list to the loaded users
    if (!this.searchTerm.trim()) {
      this.filteredUsers = this.users;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredUsers = this.users.filter(user =>
        user.name.toLowerCase().includes(term) ||
        user.email.toLowerCase().includes(term) ||
        user.userCode.toLowerCase().includes(term)
      );
    }
  }
  

  onStatusChange() {
    this.currentPage = 0; // Reset to first page when filter changes
    this.loadUsers();
  }
  loadCurrentUser() {
    this.authService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
      if (user.role.authority === AUTHORITY.MainBranchLevel) {
        this.loadBranches();
      }
      this.loadUsers(); // Load users after getting current user
    });
  }
  loadBranches() {
    this.branchService.getBranches(0, 1000).subscribe({
      next: (response) => {
        this.branches = response.data.content; // Use response.data.content
      },
      error: (error) => {
        console.error('Error loading branches:', error);
      }
    });
  }
  onBranchChange() {
    this.currentPage = 0;
    this.loadUsers();
  }
  loadUsers() {
    this.loading = true;
    this.error = null;
  
    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        const emptyResponse: PagedResponse<User> = {
          content: [],
          totalElements: 0,
          totalPages: 0,
          size: this.pageSize,
          number: this.currentPage,
          numberOfElements: 0,
          first: this.currentPage === 0,
          last: this.currentPage >= this.totalPages - 1,
          empty: true
        };
  
        if (currentUser.role?.authority === AUTHORITY.MainBranchLevel) {
          if (this.selectedBranch) {
            const branchId = this.selectedBranch.id;
            if (!branchId) {
              this.error = 'Invalid branch selection';
              return of(emptyResponse);
            }
  
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
          } else {
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
          }
        } else {
          const branchId = currentUser.branch?.id;
          if (!branchId) {
            this.error = 'Invalid branch selection';
            return of(emptyResponse);
          }
  
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
        this.onSearchChange();
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
