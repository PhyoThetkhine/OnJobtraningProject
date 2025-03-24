import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HpLoanService } from '../../../../services/hp-loan.service';
import { HpLoan } from '../../../../models/hp-loan.model';
import { AuthService } from 'src/app/services/auth.service';
import { switchMap } from 'rxjs';
import { AUTHORITY } from 'src/app/models/role.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-hp-loan-list',
  standalone: true,
  imports: [CommonModule, RouterModule,FormsModule],
  templateUrl: './hp-loan-list.component.html',
  styleUrls: ['./hp-loan-list.component.css']
})
export class HpLoanListComponent implements OnInit {
  loans: HpLoan[] = [];
  selectedStatus: string | null = null;
  loading = false;
  error: string | null = null;
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  pages: number[] = [];

  constructor(
    private hpLoanService: HpLoanService,
    private toastr: ToastrService,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.loadLoans();
  }

  onStatusChange(): void {
    this.currentPage = 0;
    this.loadLoans();
  }
  loadLoans(): void {
    this.loading = true;
    this.error = null;

    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          if (this.selectedStatus) {
            return this.hpLoanService.getAllLoansByStatus(
              this.selectedStatus,
              this.currentPage,
              this.pageSize,
              'id'
            );
          } else {
            return this.hpLoanService.getAllLoans(
              this.currentPage,
              this.pageSize,
              'id'
            );
          }
        } else {
          const branchId = currentUser.branch.id;
          if (this.selectedStatus) {
            return this.hpLoanService.getAllLoansByBranchAndStatus(
              branchId,
              this.selectedStatus,
              this.currentPage,
              this.pageSize,
              'id'
            );
          } else {
            return this.hpLoanService.getAllLoansByBranch(
              branchId,
              this.currentPage,
              this.pageSize,
              'id'
            );
          }
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
        this.toastr.error(error.message || 'Error loading HP loans');
      }
    });
  }

  calculatePages() {
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
      case 'under_review':
        return 'bg-warning';
      case 'approved':
        return 'bg-success';
      case 'rejected':
        return 'bg-danger';
      case 'disbursed':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }
}
