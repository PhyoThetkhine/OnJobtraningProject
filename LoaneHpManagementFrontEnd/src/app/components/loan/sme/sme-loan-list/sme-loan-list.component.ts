import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SMELoan } from 'src/app/models/sme-loan.model';
import { SmeLoanService } from 'src/app/services/sme-loan.service';

@Component({
  selector: 'app-sme-loan-list',
  standalone: false,
  templateUrl: './sme-loan-list.component.html',
  styleUrls: ['./sme-loan-list.component.css']
})
export class SmeLoanListComponent implements OnInit {
  loans: SMELoan[] = [];
  loading = true;
  error: string | null = null;
  Math = Math;

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  pages: number[] = [];

  constructor(
    private smeLoanService: SmeLoanService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadLoans();
  }

  loadLoans(page: number = 0): void {
    this.loading = true;
    this.error = null;
    this.smeLoanService.getAllLoans(page, this.pageSize).subscribe({
      next: (response) => {
        this.loans = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = page;
        this.calculatePages();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load loans. Please try again later.';
        this.loading = false;
        console.error('Error loading loans:', error);
      }
    });
  }

  calculatePages(): void {
    this.pages = Array.from({length: this.totalPages}, (_, i) => i);
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.loadLoans(page);
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'under_review':
        return 'bg-warning text-dark';
      case 'approved':
        return 'bg-success';
      case 'rejected':
        return 'bg-danger';
      case 'disbursed':
        return 'bg-info';
      case 'completed':
        return 'bg-primary';
      case 'defaulted':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  getPrincipalStatusBadgeClass(status: string): string {
    switch (status) {
      case 'paid':
        return 'bg-success';
      case 'not_paid':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  viewLoanDetails(loanId: number): void {
    this.router.navigate(['/loans/sme', loanId]);
  }

  createNewLoan(): void {
    this.router.navigate(['/loans/sme/create']);
  }
}
