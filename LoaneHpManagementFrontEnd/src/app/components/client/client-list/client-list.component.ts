import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CIF } from '../../../models/cif.model';
import { CIFService } from '../../../services/cif.service';
import { PagedResponse } from '../../../models/common.types';
import { AuthService } from 'src/app/services/auth.service';
import { switchMap } from 'rxjs';
import { AUTHORITY } from 'src/app/models/role.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule,FormsModule]
})
export class ClientListComponent implements OnInit {
  clients: CIF[] = [];
  loading = true;
  selectedStatus: string | null = null;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  constructor(private cifService: CIFService,    private authService: AuthService) {}

  ngOnInit() {
    this.loadClients();
  }

  onStatusChange() {
    this.currentPage = 0;
    this.loadClients();
  }
  loadClients() {
    this.loading = true;
    this.error = null;

    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          if (this.selectedStatus) {
            return this.cifService.getCIFsByStatus(
              this.selectedStatus,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          } else {
            return this.cifService.getCIFs(
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          }
        } else {
          const branchId = currentUser.branch.id;
          if (this.selectedStatus) {
            return this.cifService.getCIFsByBranchAdStatus(
              this.selectedStatus,
              branchId,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          } else {
            return this.cifService.getCIFsByBranch(
              branchId,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          }
        }
      })
    ).subscribe({
      next: (response: PagedResponse<CIF>) => {
        this.clients = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading CIFs:', error);
        this.error = 'Failed to load clients';
        this.loading = false;
      }
    });
  }
  onPageChange(page: number) {
    this.currentPage = page;
    this.loadClients();
  }

  get pages(): number[] {
    const pages = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'terminated':
        return 'bg-danger';
    
      default:
        return 'bg-secondary';
    }
  }
}
