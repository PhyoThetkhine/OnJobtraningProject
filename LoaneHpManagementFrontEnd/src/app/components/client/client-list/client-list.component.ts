import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CIF } from '../../../models/cif.model';
import { CIFService } from '../../../services/cif.service';
import { AuthService } from 'src/app/services/auth.service';
import { switchMap } from 'rxjs/operators';
import { AUTHORITY } from 'src/app/models/role.model';
import { FormsModule } from '@angular/forms';
import { ClientreportService } from 'src/app/services/clientreport.service';
import { BranchService } from 'src/app/services/branch.service';
import { Branch } from 'src/app/models/branch.model';
import { PagedResponse } from 'src/app/models/common.types';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class ClientListComponent implements OnInit {
  clients: CIF[] = [];
  originalCifs: CIF[] = [];
  branches: Branch[] = [];
  loading = true;
  searchQuery = '';
  selectedStatus: string | null = null;
  selectedBranchId: number | null = null;
  selectedBranchName: string | null = null;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  readonly AUTHORITY = AUTHORITY;

  constructor(
    private cifService: CIFService,
    private authService: AuthService,
    private clientReportService: ClientreportService,
    private branchService: BranchService
  ) {}

  ngOnInit() {
    this.loadBranches();
    this.loadClients();
  }

  loadBranches() {
    this.branchService.getAllBranches().subscribe({
      next: (branches) => {
        this.branches = branches;
      },
      error: (error) => {
        console.error('Error loading branches:', error);
      }
    });
  }

  onStatusChange() {
    this.currentPage = 0;
    this.loadClients();
  }

  onBranchChange() {
    const selectedBranch = this.branches.find(branch => branch.id === this.selectedBranchId);
    this.selectedBranchName = selectedBranch ? selectedBranch.branchName : null;
    this.currentPage = 0;
    this.loadClients();
  }

  loadClients() {
    this.loading = true;
    this.error = null;

    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          if (this.selectedBranchId) {
            return this.selectedStatus 
              ? this.cifService.getCIFsByBranchAdStatus(
                  this.selectedStatus,
                  this.selectedBranchId,
                  this.currentPage,
                  this.pageSize,
                  this.sortBy
                )
              : this.cifService.getCIFsByBranch(
                  this.selectedBranchId,
                  this.currentPage,
                  this.pageSize,
                  this.sortBy
                );
          } else {
            return this.selectedStatus
              ? this.cifService.getCIFsByStatus(
                  this.selectedStatus,
                  this.currentPage,
                  this.pageSize,
                  this.sortBy
                )
              : this.cifService.getCIFs(
                  this.currentPage,
                  this.pageSize,
                  this.sortBy
                );
          }
        } else {
          const branchId = currentUser.branch.id;
          return this.selectedStatus
            ? this.cifService.getCIFsByBranchAdStatus(
                this.selectedStatus,
                branchId,
                this.currentPage,
                this.pageSize,
                this.sortBy
              )
            : this.cifService.getCIFsByBranch(
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
        this.originalCifs = response.content;
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
      this.clients = this.originalCifs.filter(client =>
        client.cifCode.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
    } else {
      this.clients = [...this.originalCifs];
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

  downloadClientReport(format: string): void {
    this.clientReportService.generateReport(
      format, 
      this.selectedBranchName || undefined, 
      this.selectedStatus || undefined
    );
  }
}