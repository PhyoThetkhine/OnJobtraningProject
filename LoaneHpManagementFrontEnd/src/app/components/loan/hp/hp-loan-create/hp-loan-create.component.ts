import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, finalize } from 'rxjs/operators';
import { CIFService } from '../../../../services/cif.service';
import { HpLoanService } from '../../../../services/hp-loan.service';
import { AuthService } from '../../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { CIF, DealerProduct, LOAN_STATUS, HpLoanApplication } from '../../../../models/hp-loan.model';
import { DealerProductService } from '../../../../services/dealer-product.service';
import { PagedResponse } from '../../../../models/common.types';

@Component({
  selector: 'app-hp-loan-create',
  standalone: false,
  templateUrl: './hp-loan-create.component.html',
  styleUrl: './hp-loan-create.component.css'
})
export class HpLoanCreateComponent implements OnInit {
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
  
  // For product search and selection
  productSearchTerm$ = new Subject<string>();
  productResults: DealerProduct[] = [];
  selectedProduct: DealerProduct | null = null;
  productSearchLoading = false;

  constructor(
    private fb: FormBuilder,
    private cifService: CIFService,
    private hpLoanService: HpLoanService,
    private dealerProductService: DealerProductService,
    private router: Router,
    private authService: AuthService,
    private toastr: ToastrService
  ) {
    this.createForm();
    this.loadCurrentUser();
    this.setupCIFSearch();
    this.setupProductSearch();
  }

  ngOnInit() {
    // No need to load all products on init anymore
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
        productId: ['', Validators.required],
        loanAmount: ['', [Validators.required, Validators.min(0)]],
        downPayment: ['', [Validators.required, Validators.min(0)]],
        duration: ['', [Validators.required, Validators.min(1)]]
      }),
      step2: this.fb.group({
        termsAccepted: [false, Validators.requiredTrue]
      })
    });

    // Add validator for down payment
    this.loanForm.get('step1.downPayment')?.valueChanges.subscribe(value => {
      const loanAmount = this.loanForm.get('step1.loanAmount')?.value;
      if (value >= loanAmount) {
        this.loanForm.get('step1.downPayment')?.setErrors({ exceedsLoanAmount: true });
      }
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

  private setupProductSearch() {
    this.productSearchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        this.productSearchLoading = true;
        return this.dealerProductService.getAllProductToSelect(term).pipe(
          finalize(() => this.productSearchLoading = false)
        );
      })
    ).subscribe({
      next: (products) => {
        this.productResults = products || [];
      },
      error: (error) => {
        this.error = 'Failed to load products. Please try again.';
        console.error('Error loading products:', error);
      }
    });
  }

  onCIFSearch(event: any) {
    this.searchTerm$.next(event.target.value);
  }

  onProductSearch(event: any) {
    this.productSearchTerm$.next(event.target.value);
  }

  selectCIF(cif: CIF) {
    this.selectedCIF = cif;
    this.loanForm.patchValue({
      step1: {
        cifId: cif.id
      }
    });
  }

  cancelProductSelection() {
    this.selectedProduct = null;
    this.loanForm.patchValue({
      step1: {
        productId: '',
        loanAmount: ''
      }
    });
  }

  selectProduct(product: DealerProduct) {
    this.selectedProduct = product;
    this.loanForm.patchValue({
      step1: {
        productId: product.id,
        loanAmount: product.price
      }
    });
  }

  cancelCIFSelection() {
    this.selectedCIF = null;
    this.loanForm.patchValue({
      step1: {
        cifId: ''
      }
    });
    this.cifResults = [];
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
      if (control.errors['exceedsLoanAmount']) {
        return 'Down payment cannot exceed loan amount';
      }
    }
    return '';
  }

  onSubmit() {
    if (this.loanForm.valid && this.currentStep === 2) {
      this.loading = true;
      const formData = this.loanForm.value;
      
      const loanApplication: HpLoanApplication = {
        cifId: formData.step1.cifId,
        productId: formData.step1.productId,
        loanAmount: formData.step1.loanAmount,
        downPayment: formData.step1.downPayment,
        duration: formData.step1.duration,
        createdUserId: this.currentUserId,
        status: LOAN_STATUS.PENDING
      };

      this.hpLoanService.createLoan(loanApplication)
        .pipe(finalize(() => this.loading = false))
        .subscribe({
          next: (response) => {
            this.toastr.success('HP Loan application submitted successfully');
            this.router.navigate(['/loans']);
          },
          error: (error) => {
            this.error = 'Failed to submit HP loan application. Please try again.';
            this.toastr.error(error.message || 'Failed to submit loan application');
            console.error('Error submitting loan:', error);
          }
        });
    }
  }
}
