import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HpLoan, DealerProduct } from '../../../../models/hp-loan.model';
import { DealerProductService } from '../../../../services/dealer-product.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-hp-loan-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="modal-header">
      <h4 class="modal-title">Update HP Loan</h4>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <form [formGroup]="updateForm">
        <!-- Product Section -->
        <div class="mb-4">
          <h5 class="mb-3">Product Information</h5>
          
          <!-- Product Search Box -->
          <div class="mb-3">
            <label class="form-label">Search Product</label>
            <div class="input-group">
              <input type="text" class="form-control" 
                     (input)="onProductSearch($event)"
                     placeholder="Enter product name to search..."
                     [disabled]="isSearchDisabled">
              <button class="btn btn-outline-secondary" type="button" *ngIf="selectedProduct && !isSearching"
                      (click)="startProductSearch()">
                <i class="bi bi-search"></i> Change Product
              </button>
            </div>
          </div>

          <!-- Loading State -->
          <div class="text-center my-3" *ngIf="loading">
            <div class="spinner-border spinner-border-sm text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <span class="ms-2">Loading available products...</span>
          </div>

          <!-- Product Search Results -->
          <div class="list-group mb-3" *ngIf="productResults.length > 0 && isSearching">
            <button type="button" class="list-group-item list-group-item-action"
                    *ngFor="let product of productResults"
                    (click)="selectProduct(product)">
              <div class="d-flex align-items-center">
                <div class="flex-grow-1">
                  <h6 class="mb-0">{{ product.name }}</h6>
                  <p class="mb-0 text-success">{{ formatCurrency(product.price) }}</p>
                  <small class="text-muted">{{ product.description }}</small>
                </div>
              </div>
            </button>
          </div>

          <!-- Selected Product -->
          <div class="card bg-light mb-3" *ngIf="selectedProduct && !isSearching">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h6 class="card-title mb-1">{{ selectedProduct.name }}</h6>
                  <p class="card-text mb-0 text-success">Price: {{ formatCurrency(selectedProduct.price) }}</p>
                  <p class="card-text"><small class="text-muted">{{ selectedProduct.description }}</small></p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Loan Details -->
        <div class="mb-3">
          <label class="form-label">Loan Amount</label>
          <input type="number" class="form-control" formControlName="loanAmount"
                 [class.is-invalid]="updateForm.get('loanAmount')?.invalid && 
                                   (updateForm.get('loanAmount')?.dirty || updateForm.get('loanAmount')?.touched)">
          <div class="invalid-feedback" *ngIf="updateForm.get('loanAmount')?.errors?.['required']">
            Loan amount is required
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('loanAmount')?.errors?.['min']">
            Loan amount must be greater than 0
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('loanAmount')?.errors?.['max'] || 
                                             updateForm.get('loanAmount')?.errors?.['exceedsProductPrice']">
            Loan amount cannot exceed product price ({{ formatCurrency(selectedProduct.price) }})
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Down Payment</label>
          <input type="number" class="form-control" formControlName="downPayment"
                 [class.is-invalid]="updateForm.get('downPayment')?.invalid && 
                                   (updateForm.get('downPayment')?.dirty || updateForm.get('downPayment')?.touched)">
          <div class="invalid-feedback" *ngIf="updateForm.get('downPayment')?.errors?.['required']">
            Down payment is required
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('downPayment')?.errors?.['min']">
            Down payment must be greater than 0
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('downPayment')?.errors?.['exceedsLoanAmount']">
            Down payment cannot exceed loan amount
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Duration (Months)</label>
          <input type="number" class="form-control" formControlName="duration"
                 [class.is-invalid]="updateForm.get('duration')?.invalid && 
                                   (updateForm.get('duration')?.dirty || updateForm.get('duration')?.touched)">
          <div class="invalid-feedback" *ngIf="updateForm.get('duration')?.errors?.['required']">
            Duration is required
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('duration')?.errors?.['min']">
            Duration must be at least 1 month
          </div>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">Cancel</button>
      <button type="button" class="btn btn-primary" (click)="onSubmit()" 
              [disabled]="!updateForm.valid || !selectedProduct">
        Update Loan
      </button>
    </div>
  `
})
export class HpLoanUpdateComponent implements OnInit {
  @Input() loan!: HpLoan;
  @Input() selectedProduct!: DealerProduct;

  updateForm!: FormGroup;
  loading: boolean = false;
  productSearchTerm$ = new Subject<string>();
  productResults: DealerProduct[] = [];
  isSearching: boolean = false;

  get isSearchDisabled(): boolean {
    return !this.isSearching;
  }

  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    private dealerProductService: DealerProductService
  ) {}

  ngOnInit() {
    this.createForm();
    this.setupProductSearch();
  }

  private setupProductSearch() {
    this.productSearchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        this.loading = true;
        return this.dealerProductService.getAllProductToSelect(term).pipe(
          finalize(() => this.loading = false)
        );
      })
    ).subscribe({
      next: (products) => {
        this.productResults = products || [];
      },
      error: (error) => {
        console.error('Error loading products:', error);
      }
    });
  }

  startProductSearch() {
    this.isSearching = true;
    this.productResults = [];
  }

  onProductSearch(event: any) {
    if (this.isSearching) {
      this.productSearchTerm$.next(event.target.value);
    }
  }

  selectProduct(product: DealerProduct) {
    this.selectedProduct = product;
    this.productResults = [];
    this.isSearching = false;
    
    // Update loan amount validation and value
    this.updateForm.patchValue({
      loanAmount: Math.min(this.updateForm.get('loanAmount')?.value || 0, product.price)
    });
    
    this.updateForm.get('loanAmount')?.setValidators([
      Validators.required,
      Validators.min(0),
      Validators.max(product.price)
    ]);
    this.updateForm.get('loanAmount')?.updateValueAndValidity();
  }

  private createForm() {
    this.updateForm = this.fb.group({
      loanAmount: [this.loan.loanAmount, [
        Validators.required, 
        Validators.min(0),
        Validators.max(this.selectedProduct.price)
      ]],
      downPayment: [this.loan.downPayment, [
        Validators.required, 
        Validators.min(0)
      ]],
      duration: [this.loan.duration, [
        Validators.required, 
        Validators.min(1)
      ]]
    });

    // Add validator for down payment
    this.updateForm.get('downPayment')?.valueChanges.subscribe(value => {
      const loanAmount = this.updateForm.get('loanAmount')?.value;
      if (value >= loanAmount) {
        this.updateForm.get('downPayment')?.setErrors({ exceedsLoanAmount: true });
      }
    });

    // Also validate when loan amount changes
    this.updateForm.get('loanAmount')?.valueChanges.subscribe(value => {
      const downPayment = this.updateForm.get('downPayment')?.value;
      if (downPayment >= value) {
        this.updateForm.get('downPayment')?.setErrors({ exceedsLoanAmount: true });
      }
      if (value > this.selectedProduct.price) {
        this.updateForm.get('loanAmount')?.setErrors({ exceedsProductPrice: true });
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('my-MM', {
      style: 'currency',
      currency: 'MMK'
    }).format(value);
  }

  onSubmit() {
    if (this.updateForm.valid && this.selectedProduct) {
      const updateData = {
        ...this.updateForm.value,
        productId: this.selectedProduct.id
      };
      this.activeModal.close(updateData);
    }
  }
} 