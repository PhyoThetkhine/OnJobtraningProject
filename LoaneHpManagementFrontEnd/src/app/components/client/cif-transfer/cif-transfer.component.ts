import { Component, OnInit, Input } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BranchService } from '../../../services/branch.service';
import { PaymentMethodService } from '../../../services/payment-method.service';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { Branch } from '../../../models/branch.model';
import { PaymentMethod } from '../../../models/payment-method.model';
import { CIFCurrentAccount } from '../../../models/cif-current-account.model';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { CurrentUser } from 'src/app/models/user.model';
import { AUTHORITY } from 'src/app/models/role.model';
import { BranchCurrentAccount } from 'src/app/models/branch-account.model';
import { forkJoin, map } from 'rxjs';

@Component({
  selector: 'app-cif-transfer',
  templateUrl: './cif-transfer.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule]
})
export class CifTransferComponent implements OnInit {
  @Input() cifAccount!: CIFCurrentAccount;
  
  transferForm!: FormGroup;
  loading = false;
  paymentMethods: PaymentMethod[] = [];
  branchAccounts: BranchCurrentAccount[] = [];
  currentUser: CurrentUser | null = null;

  constructor(
    private fb: FormBuilder,
    public activeModal: NgbActiveModal,
    private branchService: BranchService,
    private paymentMethodService: PaymentMethodService,
    private transactionService: TransactionService,
    private toastr: ToastrService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadPaymentMethods();
    this.loadUserAndBranches();
  }

  private initializeForm() {
    const availableBalance = this.calculateAvailableBalance();
    this.transferForm = this.fb.group({
      branchAccount: ['', Validators.required],
      paymentMethod: ['', Validators.required],
      amount: ['', [
        Validators.required,
        Validators.min(1),
        Validators.max(this.calculateAvailableBalance()),
      this.availableBalanceValidator()
      ]]
    });

    this.transferForm.get('amount')?.valueChanges.subscribe(value => {
      const available = this.calculateAvailableBalance();
      if (value > available) {
        this.transferForm.get('amount')?.setErrors({ exceedsAvailable: true });
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

  private loadUserAndBranches() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.loadBranchesBasedOnRole();
      },
      error: (error) => {
        console.error('Error loading user:', error);
        this.toastr.error('Failed to load user information');
      }
    });
  }

  private loadBranchesBasedOnRole() {
    if (!this.currentUser) return;

    if (this.currentUser.roleLevel === AUTHORITY.RegularBranchLevel) {
      this.handleRegularBranchUser();
    } else if (this.currentUser.roleLevel === AUTHORITY.MainBranchLevel) {
      this.handleMainBranchUser();
    }
  }
  public calculateAvailableBalance(): number {
    if (!this.cifAccount) return 0;
    return Math.max(this.cifAccount.balance - this.cifAccount.minAmount, 0);
  }
  private availableBalanceValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const available = this.calculateAvailableBalance();
      return control.value > available ? { exceedsAvailable: true } : null;
    };
  }
  private handleRegularBranchUser() {
    const branchId = this.currentUser?.branch?.id;
    if (branchId) {
      this.loading = true;
      this.branchService.getBranchAccount(branchId).subscribe({
        next: (response) => {
          this.branchAccounts = [response.data];
          this.transferForm.patchValue({
            branchAccount: response.data.id
          });
          this.transferForm.get('branchAccount')?.disable();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading branch account:', error);
          this.toastr.error('Failed to load branch account details');
          this.loading = false;
        }
      });
    }
  }
  private handleMainBranchUser() {
    this.loading = true;
    this.branchService.getAllBranches().subscribe({
      next: (branches) => {
        // Filter branches with valid IDs and map to requests
        const accountRequests = branches
          .filter(branch => Boolean(branch.id)&& 
          branch.status === "active" ) // Ensure ID exists
          .map(branch => {
            // TypeScript now knows branch.id is number
            return this.branchService.getBranchAccount(branch.id!).pipe(
              map(accountResponse => ({
                ...accountResponse.data,
                branch: branch
              }))
            );
          });
        

        forkJoin(accountRequests).subscribe({
          next: (accounts) => {
            this.branchAccounts = accounts;
            this.loading = false;
          },
          error: (error) => {
            console.error('Error loading branch accounts:', error);
            this.toastr.error('Failed to load branch accounts');
            this.loading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error loading branches:', error);
        this.toastr.error('Failed to load branches');
        this.loading = false;
      }
    });

  }


  onSubmit() {
    if (this.transferForm.valid && this.cifAccount) {
      this.loading = true;
      //const formValue = this.transferForm.value;
      const formValue = this.transferForm.getRawValue(); // Changed this line
      const transferData = {
        fromAccountId: this.cifAccount.id,
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
// Add this method to format currency consistently
formatCurrency(value: number | undefined): string {
  if (value === undefined) return 'MMK 0.00';
  return `MMK ${value.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')}`;
}

// Update getErrorMessage
getErrorMessage(fieldName: string): string {
  const control = this.transferForm.get(fieldName);
  
  if (control?.errors) {
    if (control.errors['required']) {
      return 'This field is required';
    }
    if (control.errors['min']) {
      return 'Amount must be greater than 0';
    }
    if (control.errors['max']) {
      return 'Amount exceeds maximum limit';
    }
    if (control?.errors) {
      if (control.errors['exceedsAvailable']) {
        return `Maximum transferable amount is ${this.formatCurrency(this.calculateAvailableBalance())}`;
      }
    }
  }
  return '';
}
}