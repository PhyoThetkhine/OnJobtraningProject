import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainCategoryService } from '../../../services/main-category.service';
import { MainCategory } from '../../../models/main-category.model';
import { ToastrService } from 'ngx-toastr';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-main-categories',
  standalone: false,
  templateUrl: './main-categories.component.html',
  styleUrl: './main-categories.component.css'
})
export class MainCategoriesComponent implements OnInit {
  categories: MainCategory[] = [];
  loading = false;
  error: string | null = null;
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  categoryForm: FormGroup;
  isSubmitting = false;
  editingCategory: MainCategory | null = null;

  constructor(
    private mainCategoryService: MainCategoryService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private fb: FormBuilder
  ) {
    this.categoryForm = this.fb.group({
      category: ['', [Validators.required, Validators.maxLength(50)]]
    });
  }

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories(page: number = 0) {
    this.loading = true;
    this.error = null;

    this.mainCategoryService.getAllMainCategories(page, this.pageSize)
      .subscribe({
        next: (response) => {
          this.categories = response.content;
          this.currentPage = page;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading main categories:', error);
          this.error = 'Failed to load main categories';
          this.loading = false;
          this.toastr.error('Failed to load main categories');
        }
      });
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.loadCategories(page);
    }
  }

  openAddModal(content: any) {
    this.editingCategory = null;
    this.categoryForm.reset();
    this.modalService.open(content, { centered: true });
  }

  openEditModal(content: any, category: MainCategory) {
    this.editingCategory = category;
    this.categoryForm.patchValue({
      category: category.category
    });
    this.modalService.open(content, { centered: true });
  }

  onSubmit() {
    if (this.categoryForm.invalid || this.isSubmitting) {
      return;
    }

    const categoryName = this.categoryForm.get('category')?.value;
    
    // Check for duplicates, excluding the current category if editing
    const isDuplicate = this.categories.some(
      cat => cat.category.toLowerCase() === categoryName.toLowerCase() &&
             (!this.editingCategory || cat.id !== this.editingCategory.id)
    );

    if (isDuplicate) {
      this.toastr.error('Category name already exists');
      return;
    }

    this.isSubmitting = true;
    
    if (this.editingCategory) {
      // Update existing category
      this.mainCategoryService.updateMainCategory(this.editingCategory.id, categoryName)
        .subscribe({
          next: () => {
            this.toastr.success('Category updated successfully');
            this.modalService.dismissAll();
            this.loadCategories(this.currentPage);
            this.isSubmitting = false;
            this.editingCategory = null;
          },
          error: (error) => {
            console.error('Error updating category:', error);
            this.toastr.error('Failed to update category');
            this.isSubmitting = false;
          }
        });
    } else {
      // Create new category
      this.mainCategoryService.createMainCategory(categoryName)
        .subscribe({
          next: () => {
            this.toastr.success('Category added successfully');
            this.modalService.dismissAll();
            this.loadCategories(this.currentPage);
            this.isSubmitting = false;
          },
          error: (error) => {
            console.error('Error creating category:', error);
            this.toastr.error('Failed to create category');
            this.isSubmitting = false;
          }
        });
    }
  }

  // Helper method for form validation
  get categoryField() {
    return this.categoryForm.get('category');
  }
}
