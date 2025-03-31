import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CIFCurrentAccountService } from '../../../services/cif-current-account.service';
import { CIFCurrentAccount } from '../../../models/cif-current-account.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-account-limit-update',
  templateUrl: './account-limit-update.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class AccountLimitUpdateComponent implements OnInit {
  @Input() account!: CIFCurrentAccount;
  accountForm!: FormGroup;
  loading = false;

  constructor(
    private formBuilder: FormBuilder,
    private accountService: CIFCurrentAccountService,
    private authService: AuthService,
    private activeModal: NgbActiveModal,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.accountForm = this.formBuilder.group({
      minAmount: [this.account.minAmount, [Validators.required, Validators.min(0)]],
      maxAmount: [this.account.maxAmount, [Validators.required, Validators.min(0)]]
    }, { validators: this.minMaxValidator });
  }

  // Add custom validator
  private minMaxValidator(control: AbstractControl): ValidationErrors | null {
    const min = control.get('minAmount')?.value;
    const max = control.get('maxAmount')?.value;
    
    if (min !== null && max !== null && max < min) {
      return { maxLessThanMin: true };
    }
    return null;
  }

  // Update error check
  getErrorMessage(field: string): string {
    const control = this.accountForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) {
        return `${field.charAt(0).toUpperCase()}${field.slice(1)} is required`;
      }
      if (control.errors['min']) {
        return `${field.charAt(0).toUpperCase()}${field.slice(1)} must be greater than or equal to 0`;
      }
    }
    
    // Check for cross-field error
    if (this.accountForm.errors?.['maxLessThanMin'] && field === 'maxAmount') {
      return 'Maximum amount must be greater than or equal to minimum amount';
    }
    
    return '';
  }

  isFieldInvalid(field: string): boolean {
    const formControl = this.accountForm.get(field);
    return formControl ? (formControl.invalid && (formControl.dirty || formControl.touched)) : false;
  }

 
  async onSubmit() {
    if (this.accountForm.valid) {
      this.loading = true;

      try {
        // Get current user
        const currentUser = await this.authService.getCurrentUser().toPromise();
        if (!currentUser) {
          throw new Error('No authenticated user found');
        }

        const accountData = {
          ...this.accountForm.value,
          updatedUserId: currentUser.id
        };

        // Update account limits
        await this.accountService.updateAccountLimits(this.account.id, accountData).toPromise();
        this.toastr.success('Account limits updated successfully');
        this.activeModal.close(true);
      } catch (error) {
        console.error('Error updating account limits:', error);
        this.toastr.error('Failed to update account limits');
      } finally {
        this.loading = false;
      }
    } else {
      Object.keys(this.accountForm.controls).forEach(key => {
        const control = this.accountForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }

  onCancel(): void {
    this.activeModal.dismiss();
  }
} 