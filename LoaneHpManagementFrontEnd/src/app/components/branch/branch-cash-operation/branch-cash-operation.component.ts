import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { Branch } from '../../../models/branch.model';
import { BranchCurrentAccount } from '../../../models/branch-account.model';
import { CashInOutTransaction } from '../../../models/cash-in-out-transaction.model';
import { CashInOutService } from '../../../services/cash-in-out.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-branch-cash-operation',
  templateUrl: './branch-cash-operation.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class BranchCashOperationComponent implements OnInit {
  @Input() branch!: Branch;
  @Input() branchAccount!: BranchCurrentAccount;
  @Input() operationType!: CashInOutTransaction.Type;

  cashForm!: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private cashInOutService: CashInOutService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.initializeForm();
  }

  private initializeForm() {
    this.cashForm = this.fb.group({
      amount: ['', [
        Validators.required,
        Validators.min(1),
        Validators.max(this.operationType === CashInOutTransaction.Type.Cash_Out ? 
          (this.branchAccount?.balance || 0) : 999999999999)
      ]],
      description: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]]
    });

    // Listen to amount changes for validation
    if (this.operationType === CashInOutTransaction.Type.Cash_Out) {
      this.cashForm.get('amount')?.valueChanges.subscribe(value => {
        if (value > (this.branchAccount?.balance || 0)) {
          this.cashForm.get('amount')?.setErrors({ insufficientBalance: true });
        }
      });
    }
  }

  onSubmit() {
    if (this.cashForm.valid) {
      this.loading = true;
      const formValue = this.cashForm.value;

      const transaction: Partial<CashInOutTransaction> = {
        type: this.operationType,
        amount:formValue.amount,
        description: formValue.description,
        branchCurrentAccount: this.branchAccount
      };

      this.cashInOutService.createTransaction(transaction).subscribe({
        next: () => {
          this.toastr.success(`${this.operationType === CashInOutTransaction.Type.Cash_In ? 'Cash in' : 'Cash out'} completed successfully`);
          this.activeModal.close(true);
        },
        error: (error) => {
          console.error('Error during cash operation:', error);
          this.toastr.error(error.error?.message || 'Operation failed');
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.cashForm);
    }
  }

  dismiss() {
    this.activeModal.dismiss();
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.cashForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.cashForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
      if (control.errors['min']) {
        return 'Amount must be greater than 0';
      }
      if (control.errors['max'] || control.errors['insufficientBalance']) {
        return 'Insufficient balance';
      }
      if (control.errors['minlength']) {
        return 'Description must be at least 5 characters';
      }
      if (control.errors['maxlength']) {
        return 'Description cannot exceed 200 characters';
      }
    }
    return '';
  }
} 