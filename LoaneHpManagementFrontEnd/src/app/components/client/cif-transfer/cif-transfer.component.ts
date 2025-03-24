import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
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
import { CurrentUser } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-cif-transfer',
  templateUrl: './cif-transfer.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule]
})
export class CifTransferComponent implements OnInit {
  transferForm!: FormGroup;
  loading = false;
  paymentMethods: PaymentMethod[] = [];
  cifAccounts: CIFCurrentAccount[] = [];
  branchAccounts: Branch[] = [];
  selectedAccount: CIFCurrentAccount | null = null;
  currentUser: CurrentUser | null = null;
  searchCifTerm: string = '';
  filteredCifAccounts: CIFCurrentAccount[] = [];
  showCifDropdown: boolean = false;

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private branchService: BranchService,
    private paymentMethodService: PaymentMethodService,
    private transactionService: TransactionService,
    private cifCurrentAccountService: CIFCurrentAccountService,
    private toastr: ToastrService,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadPaymentMethods();
    this.loadBranches();
    this.loadCurrentUser();
  }

  private initializeForm() {
    this.transferForm = this.fb.group({
      cifAccount: [null, Validators.required],
      branchAccount: ['', Validators.required],
      paymentMethod: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]]
    });

    this.transferForm.get('cifAccount')?.valueChanges.subscribe(accountId => {
      this.updateAmountValidator(accountId);
      this.onAccountSelect(accountId);
    });
  }

  private loadCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: (currentUser) => {
        this.currentUser = currentUser;
        this.loadCIFAccounts();
      },
      error: (error) => {
        console.error('Error fetching current user:', error);
        this.toastr.error('Failed to fetch current user');
      }
    });
  }

  private loadCIFAccounts() {
    if (this.currentUser?.branch?.branchCode) {
      this.cifCurrentAccountService.getBranchCIFAccounts(
        this.currentUser.branch.branchCode,
        this.currentUser.id
      ).subscribe({
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

  private loadBranches() {
    this.branchService.getAllBranches().subscribe({
      next: (branches) => {
        this.branchAccounts = branches.filter(b => 
          b.branchCode !== this.currentUser?.branch?.branchName
        );
      },
      error: (error) => {
        console.error('Error loading branches:', error);
        this.toastr.error('Failed to load branches');
      }
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

  private updateAmountValidator(accountId: number) {
    const account = this.cifAccounts.find(acc => acc.id === accountId);
    const amountControl = this.transferForm.get('amount');
    
    if (account) {
      amountControl?.setValidators([
        Validators.required,
        Validators.min(1),
        Validators.max(account.balance)
      ]);
    } else {
      amountControl?.setValidators([Validators.required, Validators.min(1)]);
    }
    
    amountControl?.updateValueAndValidity();
  }

  onSearchCifAccount() {
    const searchTerm = this.searchCifTerm.trim().toLowerCase();
    
    if (!searchTerm) {
      this.filteredCifAccounts = [];
      this.showCifDropdown = false;
      return;
    }

    this.filteredCifAccounts = this.cifAccounts.filter(account => 
      account.accCode.toLowerCase().includes(searchTerm) ||
      account.cif.name.toLowerCase().includes(searchTerm)
    ).sort((a, b) => a.accCode.localeCompare(b.accCode));

    this.showCifDropdown = this.filteredCifAccounts.length > 0;
  }

  selectCifAccount(account: CIFCurrentAccount) {
    this.transferForm.get('cifAccount')?.setValue(account.id);
    this.selectedAccount = account;
    this.searchCifTerm = account.accCode;
    this.showCifDropdown = false;
  }

  onBlurCifInput() {
    setTimeout(() => this.showCifDropdown = false, 200);
  }

  onAccountSelect(accountId: number) {
    this.selectedAccount = this.cifAccounts.find(acc => acc.id === accountId) || null;
  }

  onSubmit() {
    if (this.transferForm.valid) {
      this.loading = true;
      const formValue = this.transferForm.value;

      const transferData = {
        fromAccountId: formValue.cifAccount,
        toAccountId: formValue.branchAccount,
        fromAccountType: 'CIF',
        toAccountType: 'BRANCH',
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