import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BranchService } from '../../../services/branch.service';
import { Branch, BranchStatus } from '../../../models/branch.model';
import { ToastrService } from 'ngx-toastr';
import { ApiPagedResponse } from '../../../models/common.types';
import { BranchreportService } from 'src/app/services/branchreport.service';

@Component({
  selector: 'app-branch-list',
  standalone: false,
  templateUrl: './branch-list.component.html',
  styleUrl: './branch-list.component.css'
})
export class BranchListComponent implements OnInit {
  branches: Branch[] = [];
  loading = false;
  error: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 5;
  totalElements = 0;
  totalPages = 0;

  // Search
  searchTerm = '';

  // Add Math as a class property
  protected Math = Math;

  constructor(
    private router: Router,
    private branchService: BranchService,
    private toastr: ToastrService,
    private branchReportService : BranchreportService
  ) {}

  ngOnInit() {
    this.loadBranches();
  }

  loadBranches() {
    this.loading = true;
    this.branchService.getBranches(this.currentPage, this.pageSize).subscribe({
      next: (response: ApiPagedResponse<Branch>) => {
        this.branches = response.data.content;
        this.totalElements = response.data.totalElements;
        this.totalPages = response.data.totalPages;
        this.currentPage = response.data.number;
        this.pageSize = response.data.size;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading branches:', error);
        this.toastr.error('Failed to load branches');
        this.loading = false;
      }
    });
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadBranches();
    }
  }

  addBranch() {
    this.router.navigate(['/branches/create']);
  }

  viewDetails(branch: Branch) {
    this.router.navigate(['/branches', branch.id]);
  }

  deleteBranch(branch: Branch) {
    if (confirm('Are you sure you want to delete this branch?')) {
      this.branchService.deleteBranch(branch.id!).subscribe({
        next: () => {
          this.toastr.success('Branch deleted successfully');
          this.loadBranches();
        },
        error: (error) => {
          console.error('Error deleting branch:', error);
          this.toastr.error('Failed to delete branch');
        }
      });
    }
  }

  getStatusBadgeClass(status: BranchStatus): string {
    switch (status) {
      case BranchStatus.ACTIVE:
        return 'badge bg-success';
      case BranchStatus.TERMINATED:
        return 'badge bg-danger';
      case BranchStatus.CLOSED:
        return 'badge bg-secondary';
      default:
        return 'badge bg-secondary';
    }
  }

  getStatusText(status: BranchStatus): string {
    switch (status) {
      case BranchStatus.ACTIVE:
        return 'Active';
      case BranchStatus.TERMINATED:
        return 'Terminated';
      case BranchStatus.CLOSED:
        return 'Closed';
      default:
        return 'Unknown';
    }
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  downloadBranchReport(format : string) {
    this.branchReportService.generateReport(format);
}
}
