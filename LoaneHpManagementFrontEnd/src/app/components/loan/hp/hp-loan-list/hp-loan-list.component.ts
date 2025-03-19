import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HpLoanService } from '../../../../services/hp-loan.service';
import { HpLoan } from '../../../../models/hp-loan.model';

@Component({
  selector: 'app-hp-loan-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './hp-loan-list.component.html',
  styleUrls: ['./hp-loan-list.component.css']
})
export class HpLoanListComponent implements OnInit {
  loans: HpLoan[] = [];
  loading = false;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  pages: number[] = [];

  constructor(
    private hpLoanService: HpLoanService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.loadLoans();
  }

  loadLoans(page: number = 0) {
    this.loading = true;
    this.hpLoanService.getLoans(page, this.pageSize).subscribe({
      next: (response) => {
        this.loans = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = page;
        this.calculatePages();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading loans:', error);
        this.toastr.error(error.message || 'Failed to load loans');
        this.loading = false;
      }
    });
  }

  calculatePages() {
    this.pages = Array.from({length: this.totalPages}, (_, i) => i);
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.loadLoans(page);
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
