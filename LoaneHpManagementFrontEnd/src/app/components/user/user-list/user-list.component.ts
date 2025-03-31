import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { CurrentUser, User } from '../../../models/user.model';
import { PagedResponse } from '../../../models/common.types';
import { AuthService } from 'src/app/services/auth.service';
import { AUTHORITY } from 'src/app/models/role.model';
import { switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ReportService } from 'src/app/services/report.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule ]
})
export class UserListComponent implements OnInit {
  currentUser: CurrentUser | null = null;
  users: User[] = [];
  loading = false;
  selectedStatus: string | null = null;
  selectedBranchId: number | null = null; // Added for branch selection
  branches: { id: number; name: string }[] = []; // Define branches array
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private reportService: ReportService
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.loadCurrentUser();
    // this.loadBranches(); // Load branches if needed
  }

  onStatusChange() {
    this.currentPage = 0; // Reset to first page when filter changes
    this.loadUsers();
  }

  loadCurrentUser() {
    this.authService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
    });
  }

  loadUsers() {
    this.loading = true;
    this.error = null;
  
    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          return this.selectedStatus 
            ? this.userService.getUsersByStatus(this.selectedStatus, this.currentPage, this.pageSize, this.sortBy)
            : this.userService.getUsers(this.currentPage, this.pageSize, this.sortBy);
        } else {
          const branchId = currentUser.branch.id;
          return this.selectedStatus 
            ? this.userService.getUsersByBranchAndStatus(this.selectedStatus, branchId, this.currentPage, this.pageSize, this.sortBy)
            : this.userService.getUsersByBranch(branchId, this.currentPage, this.pageSize, this.sortBy);
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
