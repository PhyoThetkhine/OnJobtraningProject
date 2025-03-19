import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../../../../services/auth.service';

interface ConfirmData {
  disbursementAmount: number;
  documentFeeRate: number;
  serviceChargeRate: number;
  gracePeriod: number;
  interestRate: number;
  lateFeeRate: number;
  defaultRate: number;
  longTermOverdueRate: number;
  confirmUserId: number;
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-header">
      <h5 class="modal-title">Confirm Submission</h5>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <p>Are you sure you want to confirm this loan with the following details?</p>
      <div class="mt-3">
        <ul class="list-unstyled">
          <li class="mb-2">
            <strong>Disbursement Amount:</strong> {{ data.disbursementAmount | number }} MMK
          </li>
          <li class="mb-2">
            <strong>Document Fee Rate:</strong> {{ data.documentFeeRate }}%
          </li>
          <li class="mb-2">
            <strong>Service Charge Rate:</strong> {{ data.serviceChargeRate }}%
          </li>
          <li class="mb-2">
            <strong>Grace Period:</strong> {{ data.gracePeriod }} days
          </li>
          <li class="mb-2">
            <strong>Interest Rate:</strong> {{ data.interestRate }}% per year
          </li>
          <li class="mb-2">
            <strong>Late Fee Rate:</strong> {{ data.lateFeeRate }}% per day
          </li>
          <li class="mb-2">
            <strong>Default Rate:</strong> {{ data.defaultRate }}% per day
          </li>
          <li class="mb-2">
            <strong>Long Term Overdue Rate:</strong> {{ data.longTermOverdueRate }}% per day
          </li>
        </ul>
      </div>
      <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-2"></i>
        This action cannot be undone.
      </div>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">Cancel</button>
      <button type="button" class="btn btn-primary" (click)="activeModal.close(true)">
        Confirm Submission
      </button>
    </div>
  `
})
export class ConfirmDialogComponent {
  @Input() data!: ConfirmData;

  constructor(public activeModal: NgbActiveModal) {}
}

@Component({
  selector: 'app-hp-loan-confirm',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-header">
      <h5 class="modal-title">Confirm Loan</h5>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <form #confirmForm="ngForm" (ngSubmit)="onSubmit(confirmForm)">
      <div class="modal-body">
        <div class="row g-3">
          <!-- Disbursement Amount -->
          <div class="col-md-6">
            <label class="form-label">Disbursement Amount</label>
            <div class="input-group">
              <input type="number" class="form-control" name="disbursementAmount" 
                     [(ngModel)]="confirmData.disbursementAmount" 
                     required
                     min="1"
                     [max]="loanAmount"
                     #disbursementAmount="ngModel"
                     [class.is-invalid]="disbursementAmount.invalid && (disbursementAmount.dirty || disbursementAmount.touched)">
              <span class="input-group-text">MMK</span>
            </div>
            <div class="invalid-feedback" *ngIf="disbursementAmount.errors?.['required']">
              Disbursement amount is required
            </div>
            <div class="invalid-feedback" *ngIf="disbursementAmount.errors?.['min']">
              Disbursement amount must be greater than 0
            </div>
            <div class="invalid-feedback" *ngIf="disbursementAmount.errors?.['max']">
              Disbursement amount cannot exceed loan amount ({{ loanAmount | number }} MMK)
            </div>
            <small class="text-muted">
              Maximum amount: {{ loanAmount | number }} MMK
            </small>
          </div>

          <!-- Document Fee Rate -->
          <div class="col-md-6">
            <label class="form-label">Document Fee Rate (per Disbursement)</label>
            <div class="input-group">
              <input type="number" class="form-control" name="documentFeeRate" 
                     [(ngModel)]="confirmData.documentFeeRate" 
                     required
                     min="0"
                     max="100"
                     #documentFeeRate="ngModel"
                     [class.is-invalid]="documentFeeRate.invalid && (documentFeeRate.dirty || documentFeeRate.touched)">
              <span class="input-group-text">%</span>
            </div>
            <div class="invalid-feedback" *ngIf="documentFeeRate.errors?.['required']">
              Document fee rate is required
            </div>
            <div class="invalid-feedback" *ngIf="documentFeeRate.errors?.['min'] || documentFeeRate.errors?.['max']">
              Document fee rate must be between 0 and 100
            </div>
          </div>

          <!-- Service Charge Rate -->
          <div class="col-md-6">
            <label class="form-label">Service Charge Rate (per Disbursement)</label>
            <div class="input-group">
              <input type="number" class="form-control" name="serviceChargeRate" 
                     [(ngModel)]="confirmData.serviceChargeRate" 
                     required
                     min="0"
                     max="100"
                     #serviceChargeRate="ngModel"
                     [class.is-invalid]="serviceChargeRate.invalid && (serviceChargeRate.dirty || serviceChargeRate.touched)">
              <span class="input-group-text">%</span>
            </div>
            <div class="invalid-feedback" *ngIf="serviceChargeRate.errors?.['required']">
              Service charge rate is required
            </div>
            <div class="invalid-feedback" *ngIf="serviceChargeRate.errors?.['min'] || serviceChargeRate.errors?.['max']">
              Service charge rate must be between 0 and 100
            </div>
          </div>

          <!-- Grace Period -->
          <div class="col-md-6">
            <label class="form-label">Grace Period</label>
            <div class="input-group">
              <input type="number" class="form-control" name="gracePeriod" 
                     [(ngModel)]="confirmData.gracePeriod" 
                     required
                     min="0"
                     max="365"
                     #gracePeriod="ngModel"
                     [class.is-invalid]="gracePeriod.invalid && (gracePeriod.dirty || gracePeriod.touched)">
              <span class="input-group-text">days</span>
            </div>
            <div class="invalid-feedback" *ngIf="gracePeriod.errors?.['required']">
              Grace period is required
            </div>
            <div class="invalid-feedback" *ngIf="gracePeriod.errors?.['min'] || gracePeriod.errors?.['max']">
              Grace period must be between 0 and 365 days
            </div>
          </div>

          <!-- Interest Rate -->
          <div class="col-md-6">
            <label class="form-label">Interest Rate (per year)</label>
            <div class="input-group">
              <input type="number" class="form-control" name="interestRate" 
                     [(ngModel)]="confirmData.interestRate" 
                     required
                     min="0"
                     max="100"
                     #interestRate="ngModel"
                     [class.is-invalid]="interestRate.invalid && (interestRate.dirty || interestRate.touched)">
              <span class="input-group-text">%</span>
            </div>
            <div class="invalid-feedback" *ngIf="interestRate.errors?.['required']">
              Interest rate is required
            </div>
            <div class="invalid-feedback" *ngIf="interestRate.errors?.['min'] || interestRate.errors?.['max']">
              Interest rate must be between 0 and 100
            </div>
          </div>

          <!-- Late Fee Rate -->
          <div class="col-md-6">
            <label class="form-label">Late Fee Rate (per year)</label>
            <div class="input-group">
              <input type="number" class="form-control" name="lateFeeRate" 
                     [(ngModel)]="confirmData.lateFeeRate" 
                     required
                     min="0"
                     max="100"
                     step="0.01"
                     #lateFeeRate="ngModel"
                     [class.is-invalid]="lateFeeRate.invalid && (lateFeeRate.dirty || lateFeeRate.touched)">
              <span class="input-group-text">%</span>
            </div>
            <div class="invalid-feedback" *ngIf="lateFeeRate.errors?.['required']">
              Late fee rate is required
            </div>
            <div class="invalid-feedback" *ngIf="lateFeeRate.errors?.['min'] || lateFeeRate.errors?.['max']">
              Late fee rate must be between 0 and 100
            </div>
            <small class="text-muted">Applied from day 1 of late payment</small>
          </div>

          <!-- Default Rate -->
          <div class="col-md-6">
            <label class="form-label">Default Rate (per year)</label>
            <div class="input-group">
              <input type="number" class="form-control" name="defaultRate" 
                     [(ngModel)]="confirmData.defaultRate" 
                     required
                     min="0"
                     max="100"
                     step="0.01"
                     #defaultRate="ngModel"
                     [class.is-invalid]="defaultRate.invalid && (defaultRate.dirty || defaultRate.touched)">
              <span class="input-group-text">%</span>
            </div>
            <div class="invalid-feedback" *ngIf="defaultRate.errors?.['required']">
              Default rate is required
            </div>
            <div class="invalid-feedback" *ngIf="defaultRate.errors?.['min'] || defaultRate.errors?.['max']">
              Default rate must be between 0 and 100
            </div>
            <small class="text-muted">Applied after 90 days late</small>
          </div>

          <!-- Long Term Overdue Rate -->
          <div class="col-md-6">
            <label class="form-label">Long Term Overdue Rate (per year)</label>
            <div class="input-group">
              <input type="number" class="form-control" name="longTermOverdueRate" 
                     [(ngModel)]="confirmData.longTermOverdueRate" 
                     required
                     min="0"
                     max="100"
                     step="0.01"
                     #longTermOverdueRate="ngModel"
                     [class.is-invalid]="longTermOverdueRate.invalid && (longTermOverdueRate.dirty || longTermOverdueRate.touched)">
              <span class="input-group-text">%</span>
            </div>
            <div class="invalid-feedback" *ngIf="longTermOverdueRate.errors?.['required']">
              Long term overdue rate is required
            </div>
            <div class="invalid-feedback" *ngIf="longTermOverdueRate.errors?.['min'] || longTermOverdueRate.errors?.['max']">
              Long term overdue rate must be between 0 and 100
            </div>
            <small class="text-muted">Applied after 180 days late</small>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">Cancel</button>
        <button type="submit" class="btn btn-primary" [disabled]="!confirmForm.valid">
          Proceed
        </button>
      </div>
    </form>
  `
})
export class HpLoanConfirmComponent implements OnInit {
  @Input() loanAmount: number = 0;
  @Input() confirmData: ConfirmData = {
    disbursementAmount: 0,
    documentFeeRate: 0,
    serviceChargeRate: 0,
    gracePeriod: 0,
    interestRate: 0,
    lateFeeRate: 0,
    defaultRate: 0,
    longTermOverdueRate: 0,
    confirmUserId: 0
  };

  constructor(
    public activeModal: NgbActiveModal,
    private modalService: NgbModal,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Get the current user ID when component initializes
    this.authService.getCurrentUser().subscribe(user => {
      if (user) {
        this.confirmData.confirmUserId = user.id;
      }
    });
  }

  onSubmit(form: NgForm) {
    if (form.valid && this.confirmData.disbursementAmount <= this.loanAmount) {
      // Show confirmation dialog
      const modalRef = this.modalService.open(ConfirmDialogComponent, {
        centered: true,
        backdrop: 'static'
      });
      
      modalRef.componentInstance.data = {...this.confirmData};
      
      modalRef.result.then(
        (result) => {
          if (result) {
            // User confirmed, close the main modal with the data
            this.activeModal.close(this.confirmData);
          }
        },
        () => {} // Modal dismissed
      );
    }
  }
} 