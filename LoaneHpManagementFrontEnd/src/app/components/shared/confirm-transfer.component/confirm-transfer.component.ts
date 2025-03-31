// confirm-dialog.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-header">
      <h5 class="modal-title">{{ title }}</h5>
      <button type="button" class="btn-close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <p>{{ message }}</p>
      <div *ngIf="transferDetails" class="transfer-details">
        <div class="row mb-2">
          <div class="col-6 fw-bold">Amount:</div>
          <div class="col-6">{{ transferDetails.amount | currency:'MMK ' }}</div>
        </div>
        <div class="row mb-2">
          <div class="col-6 fw-bold">From Account:</div>
          <div class="col-6">{{ transferDetails.fromAccount }}</div>
        </div>
        <div class="row mb-2">
          <div class="col-6 fw-bold">To Account:</div>
          <div class="col-6">{{ transferDetails.toAccount }}</div>
        </div>
        <div class="row">
          <div class="col-6 fw-bold">Payment Method:</div>
          <div class="col-6">{{ transferDetails.paymentMethod }}</div>
        </div>
      </div>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">
        {{ cancelButtonText }}
      </button>
      <button type="button" class="btn btn-primary" (click)="activeModal.close(true)">
        {{ confirmButtonText }}
      </button>
    </div>
  `
})
export class ConfirmTransferComponent {
  @Input() title: string = 'Confirm Transfer';
  @Input() message: string = 'Are you sure you want to proceed with this transfer?';
  @Input() confirmButtonText: string = 'Confirm';
  @Input() cancelButtonText: string = 'Cancel';
  @Input() transferDetails: any;

  constructor(public activeModal: NgbActiveModal) {}
}