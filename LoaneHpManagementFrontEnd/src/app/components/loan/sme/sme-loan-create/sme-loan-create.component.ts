import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CIFService } from '../../../../services/cif.service';
import { CIF } from '../../../../models/cif.model';
import { FREQUENCY } from '../../../../models/sme-loan.model';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, switchMap, finalize } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { CollateralService } from '../../../../services/collateral.service';
import { Collateral } from '../../../../models/collateral.model';
import { AuthService } from '../../../../services/auth.service';
import { SmeLoanService } from '../../../../services/sme-loan.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sme-loan-create',
  standalone: false,
  templateUrl: './sme-loan-create.component.html',
  styleUrl: './sme-loan-create.component.css'
})
export class SmeLoanCreateComponent implements OnInit {
  currentStep = 1;
  totalSteps = 2;
  loanForm!: FormGroup;
  loading = false;
  error: string | null = null;
  
  // For CIF search and selection
  searchTerm$ = new Subject<string>();
  cifResults: CIF[] = [];
  selectedCIF: CIF | null = null;
  currentUserId: number | null = null;
  cifSearchLoading = false;
  
  // Enums for template
  FREQUENCY = FREQUENCY;
  
  // Collateral related
  maxLoanAmount: number = 0;
  selectedCollaterals: Collateral[] = [];
  availableCollaterals: Collateral[] = [];

  // Duration label
  durationLabel: string = 'Duration (Months)';

  constructor(
    private fb: FormBuilder,
    private cifService: CIFService,
    private collateralService: CollateralService,
    private router: Router,
    private authService: AuthService,
    private smeLoanService: SmeLoanService,
    private toastr: ToastrService
  ) {
    this.createForm();
    this.loadCurrentUser();
    this.setupCIFSearch();
    this.setupFrequencyChange();
  }

  ngOnInit() {
    // Initialize any required data
  }

  private loadCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserId = user.id;
      },
      error: (error) => {
        this.error = 'Failed to load user information.';
        console.error('Error loading user:', error);
      }
    });
  }

  private createForm() {
    this.loanForm = this.fb.group({
      step1: this.fb.group({
        cifId: ['', Validators.required],
        loanAmount: ['', [Validators.required, Validators.min(0)]],
        frequency: ['', Validators.required],
        duration: ['', [Validators.required, Validators.min(1)]],
        selectedCollaterals: [[]],
        purpose: ['', [Validators.required, Validators.minLength(10)]]
      }),
      step2: this.fb.group({
        termsAccepted: [false, Validators.requiredTrue]
      })
    });

    // Add validator for loan amount
    this.loanForm.get('step1.loanAmount')?.valueChanges.subscribe(value => {
      if (value > this.maxLoanAmount) {
        this.loanForm.get('step1.loanAmount')?.setErrors({ exceedsMax: true });
      }
    });
  }

  private setupFrequencyChange() {
    this.loanForm.get('step1.frequency')?.valueChanges.subscribe(frequency => {
      this.durationLabel = frequency === FREQUENCY.YEARLY ? 'Duration (Years)' : 'Duration (Months)';
    });
  }

  private setupCIFSearch() {
    this.searchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (!this.currentUserId) {
          throw new Error('Current user not loaded');
        }
        this.cifSearchLoading = true;
        return this.cifService.getCIFsToSellect(this.currentUserId, term).pipe(
          finalize(() => this.cifSearchLoading = false)
        );
      })
    ).subscribe({
      next: (response) => {
        this.cifResults = response;
      },
      error: (error) => {
        this.error = 'Failed to load CIFs. Please try again.';
        console.error('Error loading CIFs:', error);
      }
    });
  }

  onCIFSearch(event: any) {
    this.searchTerm$.next(event.target.value);
  }

  selectCIF(cif: CIF) {
    this.selectedCIF = cif;
    this.loanForm.patchValue({
      step1: {
        cifId: cif.id
      }
    });
    this.loadCollaterals(cif.id);
  }

  private loadCollaterals(cifId: number) {
    this.loading = true;
    this.collateralService.getCollateralsByCifIdToSelect(cifId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (collaterals) => {
          this.availableCollaterals = collaterals;
          this.selectedCollaterals = [];
          this.updateMaxLoanAmount();
        },
        error: (error) => {
          this.error = 'Failed to load collaterals. Please try again.';
          console.error('Error loading collaterals:', error);
        }
      });
  }

  selectCollateral(collateral: Collateral) {
    const index = this.selectedCollaterals.findIndex(c => c.id === collateral.id);
    if (index === -1) {
      this.selectedCollaterals.push(collateral);
    } else {
      this.selectedCollaterals.splice(index, 1);
    }
    this.updateMaxLoanAmount();
    this.loanForm.patchValue({
      step1: {
        selectedCollaterals: this.selectedCollaterals
      }
    });
  }

  private updateMaxLoanAmount() {
    this.maxLoanAmount = this.selectedCollaterals.reduce((sum, collateral) => {
      return sum + (collateral.estimatedValue * 0.7); // 70% of collateral value
    }, 0);

    // Revalidate loan amount if it exists
    const loanAmount = this.loanForm.get('step1.loanAmount')?.value;
    if (loanAmount && loanAmount > this.maxLoanAmount) {
      this.loanForm.get('step1.loanAmount')?.setErrors({ exceedsMax: true });
    }
  }

  nextStep() {
    if (this.currentStep === 1 && this.loanForm.get('step1')?.valid) {
      this.currentStep++;
    }
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loanForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.loanForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
      if (control.errors['min']) {
        return 'Value must be greater than 0';
      }
      if (control.errors['exceedsMax']) {
        return `Amount cannot exceed ${this.maxLoanAmount}`;
      }
      if (control.errors['minlength']) {
        return 'Input is too short';
      }
    }
    return '';
  }

  onSubmit() {
    if (this.loanForm.valid && this.currentStep === 2) {
      this.loading = true;
      const formData = this.loanForm.value;
      
      const loanApplication = {
        cifId: formData.step1.cifId,
        loanAmount: formData.step1.loanAmount,
        frequency: formData.step1.frequency,
        duration: formData.step1.duration,
        collateralIds: this.selectedCollaterals.map(c => c.id),
        purpose: formData.step1.purpose,
        createdUserId: this.currentUserId,
        status: 'PENDING'
      };

      this.smeLoanService.createLoan(loanApplication)
        .pipe(finalize(() => this.loading = false))
        .subscribe({
          next: (response) => {
            this.toastr.success('Loan application submitted successfully');
            this.router.navigate(['/loans']);
          },
          error: (error) => {
            this.error = 'Failed to submit loan application. Please try again.';
            this.toastr.error(error.message || 'Failed to submit loan application');
            console.error('Error submitting loan:', error);
          }
        });
    }
  }

  cancelCIFSelection() {
    this.selectedCIF = null;
    this.loanForm.patchValue({
      step1: {
        cifId: ''
      }
    });
    this.cifResults = [];
    this.selectedCollaterals = [];
    this.availableCollaterals = [];
    this.maxLoanAmount = 0;
  }
}
