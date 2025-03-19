import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Input } from '@angular/core';

import { SmeLoanService } from '../../../../services/sme-loan.service';
import { UserService } from '../../../../services/user.service';
import { CollateralService } from '../../../../services/collateral.service';
import { SMELoan } from '../../../../models/sme-loan.model';
import { User } from '../../../../models/user.model';
import { Collateral } from '../../../../models/collateral.model';
import { SmeLoanConfirmComponent } from '../sme-loan-confirm/sme-loan-confirm.component';
import { SmeLoanUpdateComponent } from '../sme-loan-update/sme-loan-update.component';
import { VoucherService } from '../../../../services/voucher.service';
import { ApiResponse } from '../../../../models/user.model';

@Component({
  selector: 'app-sme-loan-detail',
  templateUrl: './sme-loan-detail.component.html',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule
  ],
  styles: [`
    .collateral-image {
      min-height: 300px;
      object-fit: cover;
      object-position: center;
    }
    .card {
      transition: all 0.3s ease;
      border: 1px solid rgba(0,0,0,0.125);
    }
    .card:hover {
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
  `]
})
export class SmeLoanDetailComponent implements OnInit {
  loanId: number = 0;
  loan: SMELoan | null = null;
  loading: boolean = true;
  confirmUser: User | null = null;
  collaterals: Collateral[] = [];
  collateralsLoading = false;
  collateralsError: string | null = null;
  collateralsCurrentPage = 0;
  collateralsPageSize = 10;
  collateralsTotalElements = 0;
  collateralsTotalPages = 0;
  activeTab: 'loan-info' | 'repayment-schedule' | 'repayment-history' | 'collateral' = 'loan-info';

  constructor(
    private route: ActivatedRoute,
    private smeLoanService: SmeLoanService,
    private userService: UserService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private voucherService: VoucherService,
    private collateralService: CollateralService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.loanId = +params['id'];
      this.loadLoanDetails();
    });
  }

  loadLoanDetails(): void {
    this.loading = true;
    this.smeLoanService.getLoanById(this.loanId).subscribe({
      next: (loan) => {
        this.loan = loan;
        if (loan.confirmUser && typeof loan.confirmUser === 'number') {
          this.loadConfirmUser(loan.confirmUser);
        } else if (loan.confirmUser && typeof loan.confirmUser === 'object') {
          this.confirmUser = loan.confirmUser;
        }
        this.loadTerms();
        this.loadCollaterals();
      },
      error: (error) => {
        this.loading = false;
        this.toastr.error('Failed to load loan details');
        console.error('Error loading loan details:', error);
      }
    });
  }

  loadCollaterals(page: number = 0): void {
    if (!this.loan) return;
    
    this.collateralsLoading = true;
    this.collateralsError = null;
    
    this.collateralService.getCollateralsByLoanId(
      this.loanId,
      page,
      this.collateralsPageSize
    ).subscribe({
      next: (response) => {
        this.collaterals = response.content;
        this.collateralsCurrentPage = page;
        this.collateralsTotalElements = response.totalElements;
        this.collateralsTotalPages = response.totalPages;
        this.collateralsLoading = false;
      },
      error: (error) => {
        this.collateralsLoading = false;
        if (error.status === 404) {
          this.collaterals = [];
        } else {
          this.collateralsError = 'Failed to load collaterals';
          console.error('Error loading collaterals:', error);
        }
      }
    });
  }

  loadTerms(): void {
    if (!this.loan) {
      this.loading = false;
      return;
    }
    
    this.smeLoanService.getTermsByLoanId(this.loanId).subscribe({
      next: (terms) => {
        if (this.loan) {
          this.loan.terms = terms;
        }
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.toastr.error('Failed to load loan terms');
        console.error('Error loading loan terms:', error);
      }
    });
  }

  loadConfirmUser(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (user: User) => {
        this.confirmUser = user;
      },
      error: (error) => {
        console.error('Error loading confirm user:', error);
      }
    });
  }

  setActiveTab(tab: 'loan-info' | 'repayment-schedule' | 'repayment-history' | 'collateral'): void {
    this.activeTab = tab;
  }

  confirmLoan(): void {
    if (!this.loan) return;

    const modalRef = this.modalService.open(SmeLoanConfirmComponent, {
      size: 'lg',
      backdrop: 'static'
    });

    modalRef.componentInstance.loanAmount = this.loan.loanAmount;
    modalRef.componentInstance.confirmData = {
      disbursementAmount: this.loan.loanAmount,
      documentFeeRate: 0,
      serviceChargeRate: 0,
      gracePeriod: 0,
      interestRate: 0,
      lateFeeRate: 0,
      defaultRate: 0,
      longTermOverdueRate: 0,
      confirmUserId: 0,
      paidPrincipalStatus: 'not_paid'
    };

    modalRef.result.then(
      (confirmData) => {
        if (confirmData) {
          this.loading = true;
          this.smeLoanService.confirmLoan(this.loanId, confirmData).subscribe({
            next: (message) => {
              this.toastr.success(message);
              setTimeout(() => {
                this.loadLoanDetails();
              }, 500);
            },
            error: (error) => {
              this.loading = false;
              this.toastr.error(error.error?.message || 'Failed to confirm loan');
              console.error('Error confirming loan:', error);
            }
          });
        }
      },
      () => {}
    );
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'under_review':
        return 'bg-warning text-dark';
      case 'past_due':
        return 'bg-danger';
      case 'approved':
        return 'bg-success';
      case 'rejected':
        return 'bg-danger';
      case 'disbursed':
        return 'bg-info';
      case 'completed':
        return 'bg-primary';
      default:
        return 'bg-secondary';
    }
  }

  getTermStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'under_schedule':
        return 'bg-warning text-dark';
      case 'paid':
        return 'bg-success';
      case 'overdue':
        return 'bg-danger';
      case 'partially_paid':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }

  canTogglePrincipalStatus(): boolean {
    if (!this.loan) return false;
    const status = this.loan.status;
    return ['under_schedule', 'disbursed'].includes(status);
  }

  togglePrincipalStatus(): void {
    if (!this.loan || !this.canTogglePrincipalStatus()) return;

    const newStatus = this.loan.paidPrincipalStatus === 'paid' ? 'not_paid' : 'paid';
    const modalRef = this.modalService.open(ConfirmDialogComponent, {
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.title = 'Confirm Status Change';
    modalRef.componentInstance.message = `Are you sure you want to change the principal status to ${newStatus.replace('_', ' ')}?`;
    modalRef.componentInstance.confirmButtonText = 'Yes, Change Status';
    modalRef.componentInstance.cancelButtonText = 'No, Keep Current Status';

    modalRef.result.then(
      (result) => {
        if (result) {
          this.smeLoanService.updatePrincipalStatus(this.loan!.id, newStatus).subscribe({
            next: (response) => {
              if (this.loan) {
                this.loan.paidPrincipalStatus = newStatus;
              }
              this.toastr.success(response || `Principal status successfully updated to ${newStatus.replace('_', ' ')}`);
            },
            error: (error) => {
              this.toastr.error(error.error?.message || 'Failed to update principal status');
              console.error('Error updating principal status:', error);
            }
          });
        }
      },
      () => {}
    );
  }

  canShowVoucher(): boolean {
    return this.loan?.status !== 'under_review';
  }

  showVoucher(): void {
    if (!this.loan || !this.loan.terms) return;
    this.voucherService.showVoucher(this.loan, this.loan.terms);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('my-MM', {
      style: 'currency',
      currency: 'MMK'
    }).format(value);
  }

  onCollateralsPageChange(page: number): void {
    if (page >= 0 && page < this.collateralsTotalPages) {
      this.loadCollaterals(page);
    }
  }

  updateLoan(): void {
    if (!this.loan) return;

    const modalRef = this.modalService.open(SmeLoanUpdateComponent, {
      size: 'lg',
      backdrop: 'static'
    });

    modalRef.componentInstance.loan = this.loan;
    modalRef.componentInstance.selectedCollaterals = this.collaterals;
    modalRef.componentInstance.cifId = this.loan.cif.id;

    modalRef.result.then(
      (updateData) => {
        if (updateData) {
          this.loading = true;
          this.smeLoanService.updateLoan(this.loan!.id, updateData).subscribe({
            next: (updatedLoan) => {
              this.loan = updatedLoan;
              this.toastr.success('Loan has been updated successfully');
              this.loadLoanDetails();
            },
            error: (error) => {
              this.loading = false;
              this.toastr.error(error.error?.message || 'Failed to update loan');
              console.error('Error updating loan:', error);
            }
          });
        }
      },
      () => {} // Modal dismissed
    );
  }

  canUpdateLoan(): boolean {
    return this.loan?.status === 'under_review';
  }
}


@Component({
  selector: 'app-confirm-dialog',
  template: `
    <div class="modal-header">
      <h5 class="modal-title">{{ title }}</h5>
      <button type="button" class="btn-close" aria-label="Close" (click)="activeModal.dismiss()"></button>
    </div>
    <div class="modal-body">
      <p>{{ message }}</p>
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
export class ConfirmDialogComponent {
  @Input() title: string = '';
  @Input() message: string = '';
  @Input() confirmButtonText: string = 'Confirm';
  @Input() cancelButtonText: string = 'Cancel';

  constructor(public activeModal: NgbActiveModal) {}
} 