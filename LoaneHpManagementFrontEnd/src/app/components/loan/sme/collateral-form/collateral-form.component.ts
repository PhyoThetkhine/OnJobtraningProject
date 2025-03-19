import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Collateral } from '../../../../models/collateral.model';
import { CollateralService } from '../../../../services/collateral.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-collateral-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-header">
      <h4 class="modal-title">{{ collateral ? 'Edit' : 'Add' }} Collateral</h4>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <form #collateralForm="ngForm" (ngSubmit)="onSubmit()">
        <div class="mb-3">
          <label for="propertyType" class="form-label">Property Type</label>
          <input
            type="text"
            class="form-control"
            id="propertyType"
            name="propertyType"
            [(ngModel)]="formData.propertyType"
            required
            #propertyTypeInput="ngModel"
          >
          <div class="invalid-feedback" *ngIf="propertyTypeInput.invalid && propertyTypeInput.touched">
            Property type is required
          </div>
        </div>

        <div class="mb-3">
          <label for="documentUrl" class="form-label">Document URL</label>
          <input
            type="text"
            class="form-control"
            id="documentUrl"
            name="documentUrl"
            [(ngModel)]="formData.documentUrl"
            required
            #documentUrlInput="ngModel"
          >
          <div class="invalid-feedback" *ngIf="documentUrlInput.invalid && documentUrlInput.touched">
            Document URL is required
          </div>
        </div>

        <div class="mb-3">
          <label for="estimatedValue" class="form-label">Estimated Value (MMK)</label>
          <input
            type="number"
            class="form-control"
            id="estimatedValue"
            name="estimatedValue"
            [(ngModel)]="formData.estimatedValue"
            required
            min="0"
            #estimatedValueInput="ngModel"
          >
          <div class="invalid-feedback" *ngIf="estimatedValueInput.invalid && estimatedValueInput.touched">
            Valid estimated value is required
          </div>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">Cancel</button>
      <button type="submit" class="btn btn-primary" (click)="onSubmit()" [disabled]="!collateralForm.form.valid">
        {{ collateral ? 'Update' : 'Add' }} Collateral
      </button>
    </div>
  `
})
export class CollateralFormComponent implements OnInit {
  @Input() loanId!: number;
  @Input() collateral?: Collateral;

  formData: Partial<Collateral> = {
    propertyType: '',
    documentUrl: '',
    estimatedValue: 0
  };

  constructor(
    public activeModal: NgbActiveModal,
    private collateralService: CollateralService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    if (this.collateral) {
      this.formData = {
        propertyType: this.collateral.propertyType,
        documentUrl: this.collateral.documentUrl,
        estimatedValue: this.collateral.estimatedValue
      };
    }
  }

  onSubmit(): void {
    if (!this.loanId) {
      this.toastr.error('Loan ID is required');
      return;
    }

    const collateralData = {
      ...this.formData,
      loanId: this.loanId
    };

    if (this.collateral) {
      this.collateralService.updateCollateral(this.collateral.id, collateralData).subscribe({
        next: () => {
          this.activeModal.close(true);
        },
        error: (error) => {
          this.toastr.error('Failed to update collateral');
          console.error('Error updating collateral:', error);
        }
      });
    } else {
      this.collateralService.createCollateral(collateralData).subscribe({
        next: () => {
          this.activeModal.close(true);
        },
        error: (error) => {
          this.toastr.error('Failed to create collateral');
          console.error('Error creating collateral:', error);
        }
      });
    }
  }
} 