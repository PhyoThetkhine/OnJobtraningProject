import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DealerProductService } from '../../../services/dealer-product.service';
import { DealerProduct } from '../../../models/dealer-product.model';
import { PagedResponse } from '../../../models/common.types';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {
  products: DealerProduct[] = [];
  loading = false;
  error: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  sortBy = 'id';

  constructor(
    private dealerProductService: DealerProductService,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = null;

    this.dealerProductService.getAllDealerProducts(this.currentPage, this.pageSize, this.sortBy)
      .subscribe({
        next: (response: PagedResponse<DealerProduct>) => {
          this.products = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to load products. Please try again later.';
          this.loading = false;
          console.error('Error loading products:', error);
        }
      });
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  onSortChange(sortBy: string): void {
    this.sortBy = sortBy;
    this.loadProducts();
  }

  openAddModal(modal: any): void {
    this.modalService.open(modal, { size: 'lg', centered: true });
  }

  openEditModal(modal: any, product: DealerProduct): void {
    this.modalService.open(modal, { size: 'lg', centered: true });
  }
}
