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
import { AccountType } from '../../../models/transaction.model';
import { CurrentUser } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth.service';
import { forkJoin, map } from 'rxjs';
import { BranchCurrentAccount } from 'src/app/models/branch-account.model';

@Component({
  selector: 'app-branch-transfer',
  templateUrl: './branch-transfer.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,  FormsModule]
})
export class BranchTransferComponent implements OnInit {
  @Input() branch!: Branch;
  @Input() branchAccount!: any;

  searchCifTerm: string = '';
filteredCifAccounts: CIFCurrentAccount[] = [];
showCifDropdown: boolean = false;
  transferForm!: FormGroup;
  loading = false;
  paymentMethods: PaymentMethod[] = [];
  cifAccounts: CIFCurrentAccount[] = [];
  branchAccounts: any[] = []; // Add type when available
  selectedAccount: CIFCurrentAccount | null = null;
  accountTypes = Object.values(AccountType);
  protected AccountType = AccountType;
  currentUser: CurrentUser | null = null;

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private branchService: BranchService,
    private paymentMethodService: PaymentMethodService,
    private transactionService: TransactionService,
    private cifCurrentAccountService: CIFCurrentAccountService,
    private toastr: ToastrService,
    private authService:AuthService,
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadPaymentMethods();
    this.loadBranches();
  }

  private initializeForm() {
    this.transferForm = this.fb.group({
      accountType: [AccountType.CIF, Validators.required],
      // cifAccount: [''],
      cifAccount: [null, Validators.required],
      branchAccount: [''],
      paymentMethod: ['', Validators.required],
      amount: ['', [
        Validators.required,
        Validators.min(1),
        Validators.max(this.branchAccount?.balance || 0)
      ]]
    });
     // Get the current user's information first
     this.authService.getCurrentUser().subscribe({
      next: (currentUser) => {
        console.log('Current role level:', currentUser.roleLevel);
        this.currentUser = currentUser;
        this.loadCIFAccounts();
   
      },
      error: (error) => {
        console.error('Error fetching current user:', error);
        this.toastr.error('Failed to fetch current user');
        this.loading = false;
      }
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

  // Add to BranchTransferComponent class
// private loadBranches() {
//   this.branchService.getAllBranches().subscribe({
//     next: (branches) => {
//       // Filter out current branch if needed
//       this.branchAccounts = branches
//         .filter(b => b.id !== this.branch?.id) // Exclude current branch
//         .map(branch => ({
          
//           id: branch.id,
//           branchName: branch.branchName,
//           branchCode: branch.branchCode
//         }));
//     },
//     error: (error) => {
//       console.error('Error loading branches:', error);
//       this.toastr.error('Failed to load branches');
//     }
//   });
// }
private loadBranches() {
  // First, load all branches
  this.branchService.getAllBranches().subscribe({
    next: (branches: Branch[]) => {
      // Exclude the current branch
      const targetBranches = branches.filter(b => b.id !== this.branch?.id);
      
      // For each branch, call getBranchAccount to get its current account details
      const accountObservables = targetBranches.map(branch =>
        this.branchService.getBranchAccount(branch.id!).pipe(
          map(response => {
            const branchAccount = response.data as BranchCurrentAccount;
            return {
              accountId: branchAccount.id,  // This is the branch account id
              branchName: branch.branchName,
              branchCode: branch.branchCode
            };
          })
        )
      );
      
      // Wait for all branch account calls to finish
      forkJoin(accountObservables).subscribe({
        next: (accounts) => {
          this.branchAccounts = accounts;
        },
        error: (error) => {
          console.error('Error fetching branch accounts:', error);
          this.toastr.error('Failed to load branch accounts');
        }
      });
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

  private loadCIFAccounts() {
    this.filteredCifAccounts = [];
    if (!this.currentUser) {
      console.error('Current user not available');
      return;
    }
    if (this.branch?.branchCode) {
      this.cifCurrentAccountService.getBranchCIFAccounts(this.branch.branchCode,this.currentUser?.id).subscribe({
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
  
  onSearchCifAccount() {
    const searchTerm = this.searchCifTerm.trim();
    console.log('Raw search term:', searchTerm); // Add this for debugging
    
    if (!searchTerm) {
      this.filteredCifAccounts = [];
      this.showCifDropdown = false;
      return;
    }
  
    // Match exact account code with case sensitivity
    const exactMatch = this.cifAccounts.find(account => 
      account.accCode === searchTerm // Remove .toLowerCase()
    );
  
    if (exactMatch) {
      console.log('Exact case-sensitive match found:', exactMatch);
      this.filteredCifAccounts = [exactMatch];
      this.showCifDropdown = true;
      return;
    }
  
    // For partial matches, use case-insensitive search
    const searchTermLower = searchTerm.toLowerCase();
    this.filteredCifAccounts = this.cifAccounts
      .filter(account => {
        const codeMatch = account.accCode.toLowerCase().includes(searchTermLower);
        const nameMatch = account.cif.name.toLowerCase().includes(searchTermLower);
        return codeMatch || nameMatch;
      })
      .sort((a, b) => {
        const aStarts = a.accCode.toLowerCase().startsWith(searchTermLower);
        const bStarts = b.accCode.toLowerCase().startsWith(searchTermLower);
        
        if (aStarts && !bStarts) return -1;
        if (!aStarts && bStarts) return 1;
        return a.accCode.localeCompare(b.accCode);
      });
  
    console.log('Filtered results:', this.filteredCifAccounts);
    this.showCifDropdown = this.filteredCifAccounts.length > 0;
  }

  selectCifAccount(account: CIFCurrentAccount) {
    this.transferForm.get('cifAccount')?.setValue(account.id);
    this.selectedAccount = account;
    this.searchCifTerm = account.accCode; // Show account code in input
    this.showCifDropdown = false;
  }
  
  onBlurCifInput() {
    setTimeout(() => {
      this.showCifDropdown = false;
    }, 200);
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