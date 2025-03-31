import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CIF } from '../../../models/cif.model';
import { CIFService } from '../../../services/cif.service';
import { PagedResponse } from '../../../models/common.types';
import { AuthService } from 'src/app/services/auth.service';
import { of, switchMap } from 'rxjs';
import { AUTHORITY } from 'src/app/models/role.model';
import { FormsModule } from '@angular/forms';
import { BranchService } from 'src/app/services/branch.service';
import { Branch } from 'src/app/models/branch.model';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class ClientListComponent implements OnInit {
  branches: Branch[] = [];
  readonly AUTHORITY = AUTHORITY;
  selectedBranch: Branch | null = null;
  currentUser: any = null;
originalcifs: CIF[] = [];
  clients: CIF[] = [];
  searchQuery: string = '';
  loading = true;
  selectedStatus: string | null = null;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  constructor(
    private cifService: CIFService,
    private authService: AuthService,
    private branchService: BranchService
  ) {}

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        if (user.role?.authority === AUTHORITY.MainBranchLevel) {
          this.loadBranches();
        }
        this.loadClients();
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

  onStatusChange() {
    this.currentPage = 0;
    this.loadClients();
  }

  onBranchChange() {
    this.currentPage = 0;
    this.loadClients();
  }

  loadClients() {
    this.loading = true;
    this.error = null;

    const emptyResponse: PagedResponse<CIF> = {
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
              return this.cifService.getCIFsByBranchAdStatus(
                this.selectedStatus,
                branchId,
                this.currentPage,
                this.pageSize,
                this.sortBy
              );
            }
            return this.cifService.getCIFsByBranch(
              branchId,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          } else {
            if (this.selectedStatus) {
              return this.cifService.getCIFsByStatus(
                this.selectedStatus,
                this.currentPage,
                this.pageSize,
                this.sortBy
              );
            }
            return this.cifService.getCIFs(
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          }
        } else {
          const branchId = currentUser.branch?.id;
          if (!branchId) {
            this.error = 'Invalid branch information';
            return of(emptyResponse);
          }

          if (this.selectedStatus) {
            return this.cifService.getCIFsByBranchAdStatus(
              this.selectedStatus,
              branchId,
              this.currentPage,
              this.pageSize,
              this.sortBy
            );
          }
          return this.cifService.getCIFsByBranch(
            branchId,
            this.currentPage,
            this.pageSize,
            this.sortBy
          );
        }
      })
    ).subscribe({
      next: (response: PagedResponse<CIF>) => {
        this.clients = response.content;
        this.originalcifs = response.content; 
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
  
  onSearch() {
    if (this.searchQuery) {
      this.clients = this.originalcifs.filter(clients =>
        clients.cifCode.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    } else {
      this.clients = [...this.originalcifs]; // Reset to the original list if the search query is empty
    }
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadClients();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active': return 'bg-success';
      case 'terminated': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }
}