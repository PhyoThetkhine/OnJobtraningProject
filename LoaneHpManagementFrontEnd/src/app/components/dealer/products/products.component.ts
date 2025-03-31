import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DealerProductService } from '../../../services/dealer-product.service';
import { DealerProduct } from '../../../models/dealer-product.model';
import { PagedResponse } from '../../../models/common.types';
import { SubCategoryService } from 'src/app/services/sub-category.service';
import { CIFService } from 'src/app/services/cif.service';
import { SubCategory } from 'src/app/models/sub-category.model';
import { CIF } from 'src/app/models/cif.model';

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

  // CIF filter
  cifId: number | null = null;

  // Dropdown data
  cifs: CIF[] = [];
  subCategories: SubCategory[] = [];

  // Form
  productForm: FormGroup;
  isSubmitting = false;
  editingProduct: DealerProduct | null = null;
  toastr: any;

  constructor(
    private dealerProductService: DealerProductService,
    private subCategoryService: SubCategoryService,
    private cifService: CIFService,
    private modalService: NgbModal,
    private fb: FormBuilder
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(50)]],
      price: [0, [Validators.required, Validators.min(0)]],
      description: ['', [Validators.required, Validators.maxLength(255)]],
      subCategoryId: [null, Validators.required],
      cifId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadProducts();
    this.loadDropdownData();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = null;

    if (this.cifId) {
      this.dealerProductService.getDealerProductsByCifId(this.cifId).subscribe({
        next: (data) => {
          this.products = data;
          this.totalElements = data.length;
          this.totalPages = 1;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load products.';
          this.loading = false;
          this.toastr.error('Failed to load products.');
        }
      });
    } else {
      this.dealerProductService.getAllDealerProducts(this.currentPage, this.pageSize, this.sortBy).subscribe({
        next: (response: PagedResponse<DealerProduct>) => {
          this.products = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load products.';
          this.loading = false;
          this.toastr.error('Failed to load products.');
        }
      });
    }
  }

  loadDropdownData(): void {
    this.cifService.getCIFs(0, 1000).subscribe({
      next: (response) => this.cifs = response.content,
      error: (err) => this.toastr.error('Failed to load CIFs.')
    });

    this.subCategoryService.getAllSubCategories(0, 1000).subscribe({
      next: (response) => this.subCategories = response.content.filter(sc => sc.status === 'active'),
      error: (err) => this.toastr.error('Failed to load subcategories.')
    });
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages && !this.cifId) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  onSortChange(sortBy: string): void {
    this.sortBy = sortBy;
    this.loadProducts();
  }

  openAddModal(modal: any): void {
    this.editingProduct = null;
    this.productForm.reset();
    this.modalService.open(modal, { size: 'lg', centered: true });
  }

  openEditModal(modal: any, product: DealerProduct): void {
    this.editingProduct = product;
    this.productForm.patchValue({
      name: product.name,
      price: product.price,
      description: product.description,
      subCategoryId: product.subCategory?.id,
      cifId: product.cif?.id
    });
    this.modalService.open(modal, { size: 'lg', centered: true });
  }

  onSubmit(modal: any): void {
    if (this.productForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    const { name, price, description, subCategoryId, cifId } = this.productForm.value;

    if (this.editingProduct) {
      const updatedProduct: DealerProduct = {
        ...this.editingProduct,
        name,
        price,
        description,
        subCategory: this.subCategories.find(sc => sc.id === subCategoryId) || null,
        cif: this.cifs.find(c => c.id === cifId) || null
      };
      this.dealerProductService.updateDealerProduct(this.editingProduct.id, updatedProduct).subscribe({
        next: (updated) => {
          this.toastr.success('Product updated successfully.');
          this.modalService.dismissAll();
          this.loadProducts();
          this.isSubmitting = false;
          this.editingProduct = null;
        },
        error: (err) => {
          this.toastr.error('Failed to update product.');
          this.isSubmitting = false;
        }
      });
    } else {
      const newProduct: DealerProduct = {
        id: 0,
        name,
        price,
        description,
        subCategory: this.subCategories.find(sc => sc.id === subCategoryId) || null,
        cif: this.cifs.find(c => c.id === cifId) || null,
        createdUser: null,
        createdDate: null,
        updatedDate: null
      };
      this.dealerProductService.createDealerProduct(cifId, newProduct).subscribe({
        next: (product) => {
          this.toastr.success('Product added successfully.');
          this.modalService.dismissAll();
          this.loadProducts();
          this.isSubmitting = false;
        },
        error: (err) => {
          this.toastr.error('Failed to create product.');
          this.isSubmitting = false;
        }
      });
    }
  }

  deleteProduct(id: number): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.dealerProductService.deleteDealerProduct(id).subscribe({
        next: () => {
          this.toastr.success('Product deleted successfully.');
          this.loadProducts();
        },
        error: (err) => {
          this.toastr.error('Failed to delete product.');
        }
      });
    }
  }

  setCifId(cifId: number | null): void {
    this.cifId = cifId;
    this.currentPage = 0;
    this.loadProducts();
  }

  // Form getters
  get nameField() { return this.productForm.get('name'); }
  get priceField() { return this.productForm.get('price'); }
  get descriptionField() { return this.productForm.get('description'); }
  get subCategoryField() { return this.productForm.get('subCategoryId'); }
  get cifField() { return this.productForm.get('cifId'); }
}