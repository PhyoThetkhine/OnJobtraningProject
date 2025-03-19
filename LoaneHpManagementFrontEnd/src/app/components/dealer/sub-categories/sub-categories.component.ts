import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubCategoryService } from '../../../services/sub-category.service';
import { MainCategoryService } from '../../../services/main-category.service';
import { SubCategory } from '../../../models/sub-category.model';
import { MainCategory } from '../../../models/main-category.model';
import { ToastrService } from 'ngx-toastr';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-sub-categories',
  standalone: false,
  templateUrl: './sub-categories.component.html',
  styleUrl: './sub-categories.component.css'
})
export class SubCategoriesComponent implements OnInit {
  categories: SubCategory[] = [];
  mainCategories: MainCategory[] = [];
  loading = false;
  error: string | null = null;
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  categoryForm: FormGroup;
  isSubmitting = false;
  editingCategory: SubCategory | null = null;

  constructor(
    private subCategoryService: SubCategoryService,
    private mainCategoryService: MainCategoryService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private fb: FormBuilder
  ) {
    this.categoryForm = this.fb.group({
      category: ['', [Validators.required, Validators.maxLength(50)]],
      mainCategoryId: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadCategories();
    this.loadMainCategories();
  }

  loadCategories(page: number = 0) {
    this.loading = true;
    this.error = null;

    this.subCategoryService.getAllSubCategories(page, this.pageSize)
      .subscribe({
        next: (response) => {
          this.categories = response.content;
          this.currentPage = page;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading sub categories:', error);
          this.error = 'Failed to load sub categories';
          this.loading = false;
          this.toastr.error('Failed to load sub categories');
        }
      });
  }

  loadMainCategories() {
    this.mainCategoryService.getAllMainCategories(0, 100)
      .subscribe({
        next: (response) => {
          this.mainCategories = response.content;
        },
        error: (error) => {
          console.error('Error loading main categories:', error);
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

  openEditModal(content: any, category: SubCategory) {
    this.editingCategory = category;
    this.categoryForm.patchValue({
      category: category.category,
      mainCategoryId: category.mainCategory.id
    });
    this.modalService.open(content, { centered: true });
  }

  onSubmit() {
    if (this.categoryForm.invalid || this.isSubmitting) {
      return;
    }

    const categoryName = this.categoryForm.get('category')?.value;
    const mainCategoryId = this.categoryForm.get('mainCategoryId')?.value;
    
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
      this.subCategoryService.updateSubCategory(this.editingCategory.id, categoryName, mainCategoryId)
        .subscribe({
          next: () => {
            this.toastr.success('Sub category updated successfully');
            this.modalService.dismissAll();
            this.loadCategories(this.currentPage);
            this.isSubmitting = false;
            this.editingCategory = null;
          },
          error: (error) => {
            console.error('Error updating sub category:', error);
            this.toastr.error('Failed to update sub category');
            this.isSubmitting = false;
          }
        });
    } else {
      // Create new category
      this.subCategoryService.createSubCategory(categoryName, mainCategoryId)
        .subscribe({
          next: () => {
            this.toastr.success('Sub category added successfully');
            this.modalService.dismissAll();
            this.loadCategories(this.currentPage);
            this.isSubmitting = false;
          },
          error: (error) => {
            console.error('Error creating sub category:', error);
            this.toastr.error('Failed to create sub category');
            this.isSubmitting = false;
          }
        });
    }
  }

  // Helper methods for form validation
  get categoryField() {
    return this.categoryForm.get('category');
  }

  get mainCategoryField() {
    return this.categoryForm.get('mainCategoryId');
  }
}
