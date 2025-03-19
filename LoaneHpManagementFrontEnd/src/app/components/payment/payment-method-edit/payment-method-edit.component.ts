import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PaymentMethod, PaymentMethodStatus } from '../../../models/payment-method.model';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-payment-method-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-header">
      <h5 class="modal-title">{{ isNew ? 'Add New Payment Method' : 'Edit Payment Method' }}</h5>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <form #paymentMethodForm="ngForm">
        <div class="mb-3">
          <label for="paymentType" class="form-label">Payment Type</label>
          <input
            type="text"
            class="form-control"
            id="paymentType"
            name="paymentType"
            [(ngModel)]="paymentMethod.paymentType"
            required
            #paymentTypeInput="ngModel">
          <div class="invalid-feedback" [class.d-block]="paymentTypeInput.invalid && paymentTypeInput.touched">
            Payment type is required
          </div>
        </div>
        
        <!-- Add more fields as needed -->
        
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.dismiss()">Cancel</button>
      <button 
        type="button" 
        class="btn btn-primary" 
        (click)="save()"
        [disabled]="!paymentMethodForm.form.valid">
        {{ isNew ? 'Create' : 'Save Changes' }}
      </button>
    </div>
  `
})
export class PaymentMethodEditComponent implements OnInit {
  @Input() paymentMethod!: PaymentMethod;
  @Input() isNew: boolean = false;

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
    if (this.isNew) {
      this.paymentMethod = {
        id: 0,
        paymentType: '',
        status: PaymentMethodStatus.ACTIVE,
        createdDate: new Date().toISOString(),
        updatedDate: new Date().toISOString(),
        createdUser: {} as User // Initialize with empty User object
      };
    }
  }

  save(): void {
    this.activeModal.close(this.paymentMethod);
  }
} 