import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SMELoan, FREQUENCY } from '../../../../models/sme-loan.model';
import { Collateral } from '../../../../models/collateral.model';
import { CollateralService } from '../../../../services/collateral.service';

@Component({
  selector: 'app-sme-loan-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="modal-header">
      <h4 class="modal-title">Update SME Loan</h4>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <form [formGroup]="updateForm">
        <!-- Collateral Section -->
        <div class="mb-4">
          <h5 class="mb-3">Collaterals</h5>
          
          <!-- Loading State -->
          <div class="text-center my-3" *ngIf="loading">
            <div class="spinner-border spinner-border-sm text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <span class="ms-2">Loading available collaterals...</span>
          </div>

          <!-- Available Collaterals -->
          <div class="row g-3 mb-4" *ngIf="!loading">
            <div class="col-md-6" *ngFor="let collateral of availableCollaterals">
              <div class="card h-100" 
                   [class.border-primary]="isCollateralSelected(collateral)"
                   style="cursor: pointer;"
                   (click)="toggleCollateral(collateral)">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start">
                    <div>
                      <h6 class="card-title mb-2">{{ collateral.propertyType }}</h6>
                      <p class="card-text mb-1">Estimated Value: {{ formatCurrency(collateral.estimatedValue) }}</p>
                      <small class="text-muted">Max Loan: {{ formatCurrency(collateral.estimatedValue * 0.7) }}</small>
                    </div>
                    <div class="form-check">
                      <input class="form-check-input" type="checkbox" 
                             [checked]="isCollateralSelected(collateral)"
                             (click)="$event.stopPropagation()">
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Selected Collaterals Summary -->
          <div class="card bg-light">
            <div class="card-body">
              <h6 class="card-title mb-3">Selected Collaterals Summary</h6>
              <div class="alert alert-info mb-3">
                <i class="bi bi-info-circle me-2"></i>
                Maximum loan amount based on collateral: {{ formatCurrency(maxLoanAmount) }}
              </div>
              
              <div class="list-group">
                <div class="list-group-item" *ngFor="let collateral of selectedCollaterals">
                  <div class="d-flex justify-content-between align-items-center">
                    <div>
                      <h6 class="mb-1">{{ collateral.propertyType }}</h6>
                      <p class="mb-1">Estimated Value: {{ formatCurrency(collateral.estimatedValue) }}</p>
                      <small>Max Loan Value: {{ formatCurrency(collateral.estimatedValue * 0.7) }}</small>
                    </div>
                  </div>
                </div>
                <div class="list-group-item text-center text-muted" *ngIf="selectedCollaterals.length === 0">
                  No collaterals selected
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
          <div class="invalid-feedback" *ngIf="updateForm.get('loanAmount')?.errors?.['exceedsMax']">
            Loan amount cannot exceed {{ formatCurrency(maxLoanAmount) }}
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Frequency</label>
          <select class="form-select" formControlName="frequency"
                  [class.is-invalid]="updateForm.get('frequency')?.invalid && 
                                    (updateForm.get('frequency')?.dirty || updateForm.get('frequency')?.touched)">
            <option [value]="FREQUENCY.MONTHLY">Monthly</option>
            <option [value]="FREQUENCY.YEARLY">Yearly</option>
          </select>
          <div class="invalid-feedback" *ngIf="updateForm.get('frequency')?.errors?.['required']">
            Frequency is required
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Duration</label>
          <input type="number" class="form-control" formControlName="duration"
                 [class.is-invalid]="updateForm.get('duration')?.invalid && 
                                   (updateForm.get('duration')?.dirty || updateForm.get('duration')?.touched)">
          <div class="invalid-feedback" *ngIf="updateForm.get('duration')?.errors?.['required']">
            Duration is required
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('duration')?.errors?.['min']">
            Duration must be at least 1
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Purpose</label>
          <textarea class="form-control" formControlName="purpose" rows="3"
                    [class.is-invalid]="updateForm.get('purpose')?.invalid && 
                                      (updateForm.get('purpose')?.dirty || updateForm.get('purpose')?.touched)"></textarea>
          <div class="invalid-feedback" *ngIf="updateForm.get('purpose')?.errors?.['required']">
            Purpose is required
          </div>
          <div class="invalid-feedback" *ngIf="updateForm.get('purpose')?.errors?.['minlength']">
            Purpose must be at least 10 characters
          </div>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">Cancel</button>
      <button type="button" class="btn btn-primary" (click)="onSubmit()" 
              [disabled]="!updateForm.valid || selectedCollaterals.length === 0">
        Update Loan
      </button>
    </div>
  `
})
export class SmeLoanUpdateComponent implements OnInit {
  @Input() loan!: SMELoan;
  @Input() selectedCollaterals: Collateral[] = [];
  @Input() cifId!: number;

  updateForm!: FormGroup;
  FREQUENCY = FREQUENCY;
  maxLoanAmount: number = 0;
  loading = false;
  availableCollaterals: Collateral[] = [];

  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    private collateralService: CollateralService
  ) {}

  ngOnInit() {
    this.calculateMaxLoanAmount();
    this.createForm();
    this.loadAvailableCollaterals();
  }

  private loadAvailableCollaterals() {
    this.loading = true;
    this.collateralService.getCollateralsByCifIdToSelect(this.cifId).subscribe({
      next: (collaterals) => {
        this.availableCollaterals = collaterals;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading collaterals:', error);
        this.loading = false;
      }
    });
  }

  toggleCollateral(collateral: Collateral) {
    if (this.isCollateralSelected(collateral)) {
      this.removeCollateral(collateral);
    } else {
      this.addCollateral(collateral);
    }
  }

  addCollateral(collateral: Collateral) {
    if (!this.isCollateralSelected(collateral)) {
      this.selectedCollaterals.push(collateral);
      this.calculateMaxLoanAmount();
      this.validateLoanAmount();
    }
  }

  removeCollateral(collateral: Collateral) {
    const index = this.selectedCollaterals.findIndex(c => c.id === collateral.id);
    if (index !== -1) {
      this.selectedCollaterals.splice(index, 1);
      this.calculateMaxLoanAmount();
      this.validateLoanAmount();
    }
  }

  isCollateralSelected(collateral: Collateral): boolean {
    return this.selectedCollaterals.some(c => c.id === collateral.id);
  }

  private calculateMaxLoanAmount() {
    this.maxLoanAmount = this.selectedCollaterals.reduce((sum, collateral) => {
      return sum + (collateral.estimatedValue * 0.7);
    }, 0);
  }

  private validateLoanAmount() {
    const loanAmount = this.updateForm?.get('loanAmount')?.value;
    if (loanAmount > this.maxLoanAmount) {
      this.updateForm?.get('loanAmount')?.setErrors({ exceedsMax: true });
    }
  }

  private createForm() {
    this.updateForm = this.fb.group({
      loanAmount: [this.loan.loanAmount, [
        Validators.required, 
        Validators.min(0)
      ]],
      frequency: [this.loan.frequency, Validators.required],
      duration: [this.loan.duration, [Validators.required, Validators.min(1)]],
      purpose: [this.loan.purpose, [Validators.required, Validators.minLength(10)]]
    });

    // Add validator for loan amount
    this.updateForm.get('loanAmount')?.valueChanges.subscribe(value => {
      if (value > this.maxLoanAmount) {
        this.updateForm.get('loanAmount')?.setErrors({ exceedsMax: true });
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
    if (this.updateForm.valid && this.selectedCollaterals.length > 0) {
      const updateData = {
        ...this.updateForm.value,
        collateralIds: this.selectedCollaterals.map(c => c.id)
      };
      this.activeModal.close(updateData);
    }
  }
} 