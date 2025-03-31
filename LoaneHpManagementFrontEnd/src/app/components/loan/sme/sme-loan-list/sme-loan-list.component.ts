import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { switchMap } from 'rxjs';
import { AUTHORITY } from 'src/app/models/role.model';
import { SMELoan } from 'src/app/models/sme-loan.model';
import { AuthService } from 'src/app/services/auth.service';
import { SmeLoanService } from 'src/app/services/sme-loan.service';
import { FormsModule } from '@angular/forms';
import { BranchService } from 'src/app/services/branch.service';
import { Branch } from 'src/app/models/branch.model';
import { PagedResponse } from 'src/app/models/common.types';
import { of } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sme-loan-list',
  standalone: false,
  
  templateUrl: './sme-loan-list.component.html',
  styleUrls: ['./sme-loan-list.component.css']
})
export class SmeLoanListComponent implements OnInit {
  loans: SMELoan[] = [];
  branches: Branch[] = [];
  selectedBranch: Branch | null = null;
  selectedStatus: string | null = null;
  loading = true;
  error: string | null = null;
  currentUser: any = null;
  readonly AUTHORITY = AUTHORITY;

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  pages: number[] = [];

  constructor(
    private smeLoanService: SmeLoanService,
    private router: Router,
    private authService: AuthService,
    private branchService: BranchService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        if (user.role?.authority === AUTHORITY.MainBranchLevel) {
          this.loadBranches();
        }
        this.loadLoans();
      },
      error: (err) => {
        console.error('Error loading current user:', err);
        this.currentUser = null;
      }
    });
  }

  loadBranches() {
    this.branchService.getBranches(0, 1000).subscribe({
      next: (response) => {
        this.branches = response.data.content;
      },
      error: (error) => {
        console.error('Error loading branches:', error);
        this.branches = [];
      }
    });
  }

  onStatusChange(): void {
    this.currentPage = 0;
    this.loadLoans();
  }

  onBranchChange() {
    this.currentPage = 0;
    this.loadLoans();
  }

  loadLoans(): void {
    this.loading = true;
    this.error = null;

    const emptyResponse: PagedResponse<SMELoan> = {
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

    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (!currentUser?.role) return of(emptyResponse);

        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          if (this.selectedBranch) {
            const branchId = this.selectedBranch.id;
            if (!branchId) {
              this.error = 'Invalid branch selection';
              return of(emptyResponse);
            }

            if (this.selectedStatus) {
              return this.smeLoanService.getAllLoansByBranchAndStatus(
                branchId,
                this.selectedStatus,
                this.currentPage,
                this.pageSize,
                'id'
              );
            }
            return this.smeLoanService.getAllLoansByBranch(
              branchId,
              this.currentPage,
              this.pageSize,
              'id'
            );
          } else {
            if (this.selectedStatus) {
              return this.smeLoanService.getAllLoansByStatus(
                this.selectedStatus,
                this.currentPage,
                this.pageSize,
                'id'
              );
            }
            return this.smeLoanService.getAllLoans(
              this.currentPage,
              this.pageSize,
              'id'
            );
          }
        } else {
          const branchId = currentUser.branch?.id;
          if (!branchId) {
            this.error = 'Invalid branch information';
            return of(emptyResponse);
          }

          if (this.selectedStatus) {
            return this.smeLoanService.getAllLoansByBranchAndStatus(
              branchId,
              this.selectedStatus,
              this.currentPage,
              this.pageSize,
              'id'
            );
          }
          return this.smeLoanService.getAllLoansByBranch(
            branchId,
            this.currentPage,
            this.pageSize,
            'id'
          );
        }
      })
    ).subscribe({
      next: (response) => {
        this.loans = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.calculatePages();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load loans. Please try again later.';
        this.loading = false;
        this.toastr.error(error.message || 'Error loading SME loans');
      }
    });
  }

  calculatePages(): void {
    this.pages = Array.from({length: this.totalPages}, (_, i) => i);
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadLoans();
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'under_review': return 'bg-warning text-dark';
      case 'approved': return 'bg-success';
      case 'rejected': return 'bg-danger';
      case 'disbursed': return 'bg-info';
      case 'completed': return 'bg-primary';
      case 'defaulted': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  viewLoanDetails(loanId: number): void {
    this.router.navigate(['/loans/sme', loanId]);
  }

  createNewLoan(): void {
    this.router.navigate(['/loans/sme/create']);
  }
}