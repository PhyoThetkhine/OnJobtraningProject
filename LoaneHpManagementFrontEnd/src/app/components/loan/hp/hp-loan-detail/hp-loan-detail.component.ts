import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { HpLoanService } from '../../../../services/hp-loan.service';
import { UserService } from '../../../../services/user.service';
import { HpLoan, HpTerm } from '../../../../models/hp-loan.model';
import { User } from '../../../../models/user.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HpLoanConfirmComponent } from '../hp-loan-confirm/hp-loan-confirm.component';
import { HpLoanUpdateComponent } from '../hp-loan-update/hp-loan-update.component';
import { VoucherService } from '../../../../services/voucher.service';
import { ApiResponse } from '../../../../models/user.model';
import { HpLoanHistory } from 'src/app/models/hp-loan-history';
import { HpLongOverPaidHistory } from 'src/app/models/hp-long-over-paid-history';

@Component({
  selector: 'app-hp-loan-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './hp-loan-detail.component.html',
  styleUrls: ['./hp-loan-detail.component.css']
})
export class HpLoanDetailComponent implements OnInit {
  loanId!: number;
  loan: HpLoan | null = null;
  loading = false;
  createdUser:User | null = null;
  confirmUser: User | null = null;
  activeTab: 'loan-info' | 'repayment-schedule' | 'repayment-history' = 'loan-info';
     // Add these properties
     under90History: HpLoanHistory[] = [];
     over90History: HpLongOverPaidHistory[] = [];
     under90CurrentPage = 0;
     over90CurrentPage = 0;
     pageSize = 5;
     under90Total = 0;
     over90Total = 0;
     loadingUnder = false;
     loadingOver = false;
  

  constructor(
    private route: ActivatedRoute,
    private hpLoanService: HpLoanService,
    private userService: UserService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private voucherService: VoucherService
  ) {}

  ngOnInit() {
    this.loanId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadLoanDetails();
  }

  loadLoanDetails() {
    this.loading = true;
    this.hpLoanService.getLoanById(this.loanId).subscribe({
      next: (loan) => {
        this.loan = loan;
        if (loan.confirmUser) {
          // If confirmUser is a number (ID), fetch the user details
          if (typeof loan.confirmUser === 'number') {
            this.loadConfirmUser(loan.confirmUser);
          } 
          // If confirmUser is already a User object, use it directly
          else if (typeof loan.confirmUser === 'object') {
            this.confirmUser = loan.confirmUser as User;
          }
        }
        if (loan.createdUser && typeof loan.createdUser === 'number') {
          this.loadCreatedUser(loan.createdUser);
        } else if (loan.createdUser && typeof loan.createdUser === 'object') {
          this.createdUser = loan.createdUser;
        }
        this.loadTerms(() => {
          // Load histories AFTER terms are available
          this.loadHistories();
        });
      },
      error: (error) => {
        console.error('Error loading loan details:', error);
        this.toastr.error('Failed to load loan details');
        this.loading = false;
      }
    });
  }
   loadHistories() {
      this.loadUnder90();
      this.loadOver90();
    }
    loadCreatedUser(userId: number): void {
      this.userService.getUserById(userId).subscribe({
        next: (user: User) => {
          this.createdUser = user;
        },
        error: (error) => {
          console.error('Error loading confirm user:', error);
        }
      });
    }
  
    private loadUnder90(page: number = 0) {
      this.loadingUnder = true;
      this.hpLoanService.getUnder90History(this.loanId, page, this.pageSize)
        .subscribe({
          next: (res) => {
            this.under90History = this.mapTermNumbers(res.data.content);
            this.under90Total = res.data.totalElements;
            this.under90CurrentPage = page;
            this.loadingUnder = false;
          },
          error: (err) => { /* error handling */ }
        });
    }
    private mapTermNumbers(histories: HpLoanHistory[]): HpLoanHistory[] {
      if (!this.loan?.terms) return histories;
    
      return histories.map(history => {
        // Get the term ID from nested structure
        const termId = history.hpTerm.id;
    
        // Find the index of the term in loan.terms array
        const termIndex = this.loan!.terms!.findIndex(t => t.id === termId);
    
        return {
          ...history,
          termNumber: termIndex !== -1 ? termIndex + 1 : undefined
        };
      });
    }
    
  
    private loadOver90(page: number = 0) {
      this.loadingOver = true;
      this.hpLoanService.getOver90History(this.loanId, page, this.pageSize)
        .subscribe({
          next: (res) => {
            this.over90History = res.data.content;
            this.over90Total = res.data.totalElements;
            this.over90CurrentPage = page;
            this.loadingOver = false;
          },
          error: (err) => {
            this.loadingOver = false;
            this.toastr.error('Failed to load over 90 days history');
          }
        });
    }
  
    onUnderPageChange(page: number) {
      if (page >= 0 && page < Math.ceil(this.under90Total / this.pageSize)) {
        this.loadUnder90(page);
      }
    }
  
    onOverPageChange(page: number) {
      if (page >= 0 && page < Math.ceil(this.over90Total / this.pageSize)) {
        this.loadOver90(page);
      }
    }
  

    loadTerms(callback?: () => void): void {
      this.hpLoanService.getTermsByLoanId(this.loanId).subscribe({
        next: (terms) => {
          if (this.loan) this.loan.terms = terms;
          this.loading = false;
          if (callback) callback();
        },
        error: (error) => {
          this.loading = false;
          this.toastr.error('Failed to load loan terms');
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

  setActiveTab(tab: 'loan-info' | 'repayment-schedule' | 'repayment-history') {
    this.activeTab = tab;
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'under_review':
        return 'bg-warning';
      case 'approved':
        return 'bg-success';
      case 'rejected':
        return 'bg-danger';
      case 'disbursed':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }

  getTermStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'under_schedule':
        return 'bg-info';
      case 'paid':
        return 'bg-success';
      case 'overdue':
        return 'bg-danger';
      case 'partially_paid':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }

  confirmLoan() {
    if (!this.loan) return;

    const modalRef = this.modalService.open(HpLoanConfirmComponent, {
      size: 'lg',
      backdrop: 'static'
    });

    modalRef.componentInstance.loanAmount = this.loan.loanAmount;
    modalRef.componentInstance.confirmData = {
      disbursementAmount: this.loan.loanAmount, // Set initial disbursement amount to loan amount
      documentFeeRate: 0,
      serviceChargeRate: 0,
      gracePeriod: 0,
      interestRate: 0,
      lateFeeRate: 0,
      defaultRate: 0,
      longTermOverdueRate: 0
    };

    modalRef.result.then(
      (confirmData) => {
        if (confirmData) {
          this.loading = true;
          this.hpLoanService.confirmLoan(this.loanId, confirmData).subscribe({
            next: () => {
              this.toastr.success('Loan has been confirmed successfully');
              this.loadLoanDetails();
            },
            error: (error) => {
              this.toastr.error('Failed to confirm loan: ' + error.message);
              this.loading = false;
            }
          });
        }
      },
      () => {} // Modal dismissed
    );
  }

  canShowVoucher(): boolean {
    return this.loan?.status !== 'under_review';
  }

  viewVoucher(): void {
    if (!this.loan || !this.loan.terms) return;
    this.voucherService.showVoucher(this.loan, this.loan.terms);
  }

  updateLoan() {
    if (!this.loan) return;
    
    const modalRef = this.modalService.open(HpLoanUpdateComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.loan = this.loan;
    modalRef.componentInstance.selectedProduct = this.loan.product;

    modalRef.result.then(
      (result) => {
        this.hpLoanService.updateLoan(this.loan!.id, result).subscribe({
          next: () => {
            this.toastr.success('Loan updated successfully');
            this.loadLoanDetails();
          },
          error: (error) => {
            console.error('Error updating loan:', error);
            this.toastr.error('Failed to update loan');
          }
        });
      },
      () => {} // Modal dismissed
    );
  }

  canUpdateLoan(): boolean {
    return this.loan?.status === 'under_review';
  }
} 