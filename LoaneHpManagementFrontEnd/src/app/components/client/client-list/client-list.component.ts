import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CIF } from '../../../models/cif.model';
import { CIFService } from '../../../services/cif.service';
import { PagedResponse } from '../../../models/common.types';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ClientListComponent implements OnInit {
  clients: CIF[] = [];
  loading = true;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  constructor(private cifService: CIFService) {}

  ngOnInit() {
    this.loadClients();
  }

  loadClients(page: number = 0) {
    this.loading = true;
    this.error = null;

    this.cifService.getCIFs(page, this.pageSize, this.sortBy).subscribe({
      next: (response: PagedResponse<CIF>) => {
        this.clients = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = page;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading clients:', error);
        this.error = 'Failed to load clients';
        this.loading = false;
      }
    });
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.loadClients(page);
    }
  }

  get pages(): number[] {
    const pages = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'inactive':
        return 'bg-danger';
      case 'pending':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }
}
