import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BranchService } from '../../../services/branch.service';
import { PaymentMethodService } from '../../../services/payment-method.service';
import { TransactionService } from '../../../services/transaction.service';
import { CIFCurrentAccountService } from '../../../services/cif-current-account.service';
import { ToastrService } from 'ngx-toastr';
import { Branch } from '../../../models/branch.model';
import { PaymentMethod } from '../../../models/payment-method.model';
import { CIFCurrentAccount } from '../../../models/cif-current-account.model';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AccountType } from '../../../models/transaction.model';

@Component({
  selector: 'app-branch-transfer',
  templateUrl: './branch-transfer.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class BranchTransferComponent implements OnInit {
  @Input() branch!: Branch;
  @Input() branchAccount!: any;

  transferForm!: FormGroup;
  loading = false;
  paymentMethods: PaymentMethod[] = [];
  cifAccounts: CIFCurrentAccount[] = [];
  branchAccounts: any[] = []; // Add type when available
  selectedAccount: CIFCurrentAccount | null = null;
  accountTypes = Object.values(AccountType);
  protected AccountType = AccountType;

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private branchService: BranchService,
    private paymentMethodService: PaymentMethodService,
    private transactionService: TransactionService,
    private cifCurrentAccountService: CIFCurrentAccountService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadPaymentMethods();
    this.loadCIFAccounts();
    // TODO: Load branch accounts when implementing branch-to-branch transfer
  }

  private initializeForm() {
    this.transferForm = this.fb.group({
      accountType: [AccountType.CIF, Validators.required],
      cifAccount: [''],
      branchAccount: [''],
      paymentMethod: ['', Validators.required],
      amount: ['', [
        Validators.required,
        Validators.min(1),
        Validators.max(this.branchAccount?.balance || 0)
      ]]
    });

    // Listen to account type changes
    this.transferForm.get('accountType')?.valueChanges.subscribe(type => {
      const cifAccountControl = this.transferForm.get('cifAccount');
      const branchAccountControl = this.transferForm.get('branchAccount');
      
      if (type === AccountType.CIF) {
        cifAccountControl?.setValidators(Validators.required);
        branchAccountControl?.clearValidators();
      } else {
        branchAccountControl?.setValidators(Validators.required);
        cifAccountControl?.clearValidators();
      }
      
      cifAccountControl?.updateValueAndValidity();
      branchAccountControl?.updateValueAndValidity();
    });

    // Listen to amount changes for validation
    this.transferForm.get('amount')?.valueChanges.subscribe(value => {
      if (value > (this.branchAccount?.balance || 0)) {
        this.transferForm.get('amount')?.setErrors({ insufficientBalance: true });
      }
    });

    // Listen to account changes
    this.transferForm.get('cifAccount')?.valueChanges.subscribe(value => {
      this.onAccountSelect(value);
    });
  }

  private loadPaymentMethods() {
    this.paymentMethodService.getAllPaymentMethods().subscribe({
      next: (response) => {
        this.paymentMethods = response.data.content.filter(pm => pm.status === 'active');
      },
      error: (error) => {
        console.error('Error loading payment methods:', error);
        this.toastr.error('Failed to load payment methods');
      }
    });
  }

  private loadCIFAccounts() {
    if (this.branch?.branchCode) {
      this.cifCurrentAccountService.getBranchCIFAccounts(this.branch.branchCode).subscribe({
        next: (accounts) => {
          this.cifAccounts = accounts.filter(account => account.isFreeze !== 'is_freeze');
        },
        error: (error) => {
          console.error('Error loading CIF accounts:', error);
          this.toastr.error('Failed to load client accounts');
        }
      });
    }
  }

  onAccountSelect(accountId: string | number) {
    if (accountId) {
      this.selectedAccount = this.cifAccounts.find(acc => acc.id === +accountId) || null;
    } else {
      this.selectedAccount = null;
    }
  }

  onSubmit() {
    if (this.transferForm.valid && this.branchAccount) {
      this.loading = true;
      const formValue = this.transferForm.value;

      const transferData = {
        fromAccountId: this.branchAccount.id,
        toAccountId: formValue.accountType === AccountType.CIF ? formValue.cifAccount : formValue.branchAccount,
        fromAccountType: 'BRANCH',
        toAccountType: formValue.accountType,
        amount: formValue.amount,
        paymentMethodId: formValue.paymentMethod
      };

      this.transactionService.createTransaction(transferData).subscribe({
        next: () => {
          this.toastr.success('Transfer completed successfully');
          this.activeModal.close(true);
        },
        error: (error) => {
          console.error('Error during transfer:', error);
          this.toastr.error(error.error?.message || 'Transfer failed');
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.transferForm);
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
    const field = this.transferForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.transferForm.get(fieldName);
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
    }
    return '';
  }
} 