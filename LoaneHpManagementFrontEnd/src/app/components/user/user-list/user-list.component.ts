import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { CurrentUser, User } from '../../../models/user.model';
import { PagedResponse } from '../../../models/common.types';
import { AuthService } from 'src/app/services/auth.service';
import { AUTHORITY } from 'src/app/models/role.model';
import { of, switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';

import { ReportService } from 'src/app/services/report.service';
import { BranchService } from 'src/app/services/branch.service';
import { Branch } from 'src/app/models/branch.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class UserListComponent implements OnInit {
  branches: Branch[] = [];
  readonly AUTHORITY = AUTHORITY;
  selectedBranch: Branch | null = null;
  currentUser: CurrentUser | null = null;
  users: User[] = [];
  originalUsers: User[] = []; // Add this property to store the original list of users
  loading = false;
  selectedStatus: string | null = null;
  selectedBranchId: number | null = null; // Added for branch selection
  branchesAs: { id: number; name: string }[] = []; // Define branches array
  error: string | null = null;

  searchQuery: string = '';


  // Pagination
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';


  constructor(
    private userService: UserService,
    private authService: AuthService,
    private reportService: ReportService,
    private branchService: BranchService
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.loadCurrentUser();
    // this.loadBranches(); // Load branches if needed
  }

  onStatusChange() {
    this.currentPage = 0;
    this.loadUsers();
  }

  loadCurrentUser() {
    this.authService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
      if (user.role.authority === AUTHORITY.MainBranchLevel) {
        this.loadBranches();
      }
      this.loadUsers();
    });
  }

  loadBranches() {
    this.branchService.getBranches(0, 1000).subscribe({
      next: (response) => {
        this.branches = response.data.content;
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
        this.originalUsers = response.content; // Store the original list of users
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
  

  downloadReport(format: string) {
    this.loading = true;
    const fileExtension = format === 'pdf' ? 'pdf' : 'xls';
    const reportObservable = format === 'pdf'
      ? this.reportService.getAllUsersPdfReport()
      : this.reportService.getAllUsersExcelReport();
  
    console.log(`Generating ${format} report...`); // Log start of process
    reportObservable.subscribe({
      next: (blob: Blob) => {
        console.log('Blob received:', blob); // Log the full blob object
        console.log('Blob size:', blob.size); // Already there
        console.log('Blob type:', blob.type); // Already there
        if (blob.size === 0) {
          console.warn('Warning: Blob is empty!');
        }
        const downloadUrl = window.URL.createObjectURL(blob);
        console.log('Download URL:', downloadUrl); // Verify URL creation
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `user_report.${fileExtension}`;
        link.click();
        console.log('Download triggered');
        window.URL.revokeObjectURL(downloadUrl);
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to download report. Please try again.';
        this.loading = false;
        console.error('Download error:', err); // Already there
      }
    });
  }
    // Handle PDF reports for each status
    
  downloadReportByBranch(format: string, branchId: number | null) {
    if (!branchId) {
      this.error = 'Please select a branch.';
      return;
    }
  
    this.loading = true;
    const fileExtension = format === 'pdf' ? 'pdf' : 'xls';
    const reportObservable = format === 'pdf'
      ? this.reportService.getUserPdfReportByBranch(branchId)
      : this.reportService.getUserExcelReportByBranch(branchId);
  
    reportObservable.subscribe({
      next: (blob: Blob) => {
        const downloadUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `user_report_branch_${branchId}.${fileExtension}`;
        link.click();
        window.URL.revokeObjectURL(downloadUrl);
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to download report for branch. Please try again.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  onSearch() {
    if (this.searchQuery) {
      this.users = this.originalUsers.filter(user =>
        user.userCode.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    } else {
      this.users = [...this.originalUsers]; // Reset to the original list if the search query is empty
    }

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
    return status;
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