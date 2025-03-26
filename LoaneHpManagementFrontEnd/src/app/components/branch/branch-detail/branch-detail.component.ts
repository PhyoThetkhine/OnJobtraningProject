import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BranchService } from '../../../services/branch.service';
import { Branch, BranchStatus } from '../../../models/branch.model';
import { ApiResponse, User } from '../../../models/user.model';
import { CIF } from '../../../models/cif.model';
import { ToastrService } from 'ngx-toastr';
import { BranchCurrentAccount } from '../../../models/branch-account.model';
import { CIFType } from '../../../models/cif.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalComponent } from '../../../shared/components/confirmation-modal/confirmation-modal.component';
import { LoadingDelayService } from '../../../services/loading-delay.service';
import { BranchUpdateComponent } from '../branch-update/branch-update.component';
import { BranchTransferComponent } from '../branch-transfer/branch-transfer.component';
import { BranchCashOperationComponent } from '../branch-cash-operation/branch-cash-operation.component';
import { CashInOutTransaction } from '../../../models/cash-in-out-transaction.model';
import { TransactionService } from '../../../services/transaction.service';
import { CashInOutService } from '../../../services/cash-in-out.service';
import { Transaction } from '../../../models/transaction.model';
import { catchError, map, Observable, of, shareReplay } from 'rxjs';
import { CIFCurrentAccount } from 'src/app/models/cif-current-account.model';
import { CIFCurrentAccountService } from 'src/app/services/cif-current-account.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-branch-detail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css'],
  standalone:false
})
export class BranchDetailComponent implements OnInit {
  branch: Branch | null = null;
  users: User[] = [];
  clients: CIF[] = [];
  loading = false;
  error: string | null = null;
  activeTab = 'members'; // or 'balance'

  // Pagination for users
  userCurrentPage = 0;
  userPageSize = 5;
  userTotalElements = 0;
  userTotalPages = 0;
  userLoading = false;
  userSortBy = 'id';

  // Pagination for clients
  clientCurrentPage = 0;
  clientPageSize = 5;
  clientTotalElements = 0;
  clientTotalPages = 0;
  clientLoading = false;
  clientSortBy = 'id';

  protected Math = Math;
  protected BranchStatus = BranchStatus;

  branchAccount: BranchCurrentAccount | null = null;
  refreshing = false;

  // Add these properties for regular transactions
  transactionLoading = false;
  transactions: Transaction[] = [];
  currentPage = 0;
  pageSize = 5;
  totalElements = 0;
  totalPages = 0;

  // Add these properties for cash transactions
  cashTransactionsLoading = false;
  cashTransactions: CashInOutTransaction[] = [];
  cashCurrentPage = 0;
  cashPageSize = 5;
  cashTotalElements = 0;
  cashTotalPages = 0;
 

  constructor(
    private route: ActivatedRoute,
    private branchService: BranchService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private loadingDelayService: LoadingDelayService,
    private transactionService: TransactionService,
    private cashInOutService: CashInOutService,
    private cifService : CIFCurrentAccountService,
      private authService: AuthService,
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const branchId = +params['id'];
      this.loadBranchDetails(branchId);
    });
  }

  loadBranchDetails(branchId: number) {
    this.loading = true;
    this.branchService.getBranchById(branchId).subscribe({
      next: (branch) => {
        this.branch = branch;
        this.loadBranchUsers();
        this.loadBranchClients();
        this.loadBranchAccount();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading branch details:', error);
        this.toastr.error('Failed to load branch details');
        this.loading = false;
      }
    });
  }

  loadBranchUsers() {
    if (this.branch) {
      this.userLoading = true;
      this.branchService.getBranchUsers(
        this.branch.id!, 
        this.userCurrentPage,
        this.userPageSize,
        this.userSortBy
      ).subscribe({
        next: (response) => {
          this.users = response.data.content;
          this.userTotalElements = response.data.totalElements;
          this.userTotalPages = response.data.totalPages;
          this.userCurrentPage = response.data.number;
          this.userPageSize = response.data.size;
          this.userLoading = false;
        },
        error: (error) => {
          console.error('Error loading branch users:', error);
          this.toastr.error('Failed to load branch users');
          this.userLoading = false;
        }
      });
    }
  }

  loadBranchClients() {
    if (this.branch?.branchCode) {
      this.clientLoading = true;
      this.branchService.getBranchClients(
        this.branch.branchCode,
        this.clientCurrentPage,
        this.clientPageSize,
        this.clientSortBy
      ).subscribe({
        next: (response) => {
          this.clients = response.data.content;
          this.clientTotalElements = response.data.totalElements;
          this.clientTotalPages = response.data.totalPages;
          this.clientCurrentPage = response.data.number;
          this.clientPageSize = response.data.size;
          this.clientLoading = false;
        },
        error: (error) => {
          console.error('Error loading branch clients:', error);
          this.toastr.error('Failed to load branch clients');
          this.clientLoading = false;
        }
      });
    }
  }

  loadBranchAccount() {
    if (this.branch) {
      this.branchService.getBranchAccount(this.branch.id!).subscribe({
        next: (response) => {
          this.branchAccount = response.data;
        },
        error: (error) => {
          console.error('Error loading branch account:', error);
          this.toastr.error('Failed to load branch account details');
        }
      });
    }
  }

  onTabChange(event: any) {
    if (typeof event === 'object') {
      event = event.nextId;
    }
    
    this.activeTab = event;
    
    if ((this.activeTab === 'transactions' || this.activeTab === 'balance') && this.branch) {
      this.loadTransactions(0);
      this.loadCashTransactions(0);
    }
  }
  loadTransactions(page: number) {
    if (!this.branchAccount?.id) return;
    
    this.transactionLoading = true;
    this.transactionService.getTransactionsByBranchAccountId(this.branchAccount.id, page, this.pageSize)
      .subscribe({
        next: (response) => {
          this.transactions = response.data.content;
          console.log('Transaction response:', this.transactions);
          console.log('account id:', this.branchAccount?.id);
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
          this.currentPage = response.data.number;
          this.pageSize = response.data.size;
          this.transactionLoading = false;
        },
        error: (error) => {
          console.error('Error loading transactions:', error);
          this.toastr.error('Failed to load transactions');
          this.transactionLoading = false;
        }
      });
  }

  public hasPermission(permission: string): boolean {
    return this.authService.hasPermission(permission);
  }
  loadCashTransactions(page: number) {
    if (!this.branchAccount?.id) return;
    
    this.cashTransactionsLoading = true;
    this.cashInOutService.getTransactionsByAccountId(this.branchAccount.id, page, this.cashPageSize)
      .subscribe({
        next: (response) => {
          if (response && response.data) {
            this.cashTransactions = response.data.content;
          
            this.cashTotalPages = response.data.totalPages;
            this.cashTotalElements = response.data.totalElements;
            this.cashCurrentPage = response.data.number;
            this.cashTransactionsLoading = false;
          }
        },
        error: (error) => {
          console.error('Error loading cash transactions:', error);
          this.toastr.error('Failed to load cash transactions');
          this.cashTransactionsLoading = false;
        }
      });
  }
  private accountCodeCache = new Map<string, Observable<string>>();

getAccountCode(accountId: number, accountType: string): Observable<string> {
  const cacheKey = `${accountType.toUpperCase()}_${accountId}`;
  
  if (this.accountCodeCache.has(cacheKey)) {
    return this.accountCodeCache.get(cacheKey)!;
  }

  const request$ = this.createAccountCodeRequest(accountId, accountType).pipe(
    shareReplay(1),
    catchError(() => of('N/A'))
  );

  this.accountCodeCache.set(cacheKey, request$);
  return request$;
}

private createAccountCodeRequest(accountId: number, accountType: string): Observable<string> {
  switch(accountType.toUpperCase()) {
    case 'CIF':
      return this.cifService.getAccountById(accountId).pipe(
        map((response: CIFCurrentAccount) => response.accCode),
        catchError(() => of('CIF Not Found'))
      );

    case 'BRANCH':
      return this.branchService.getBranchAccountById(accountId).pipe(
        map((response: ApiResponse<BranchCurrentAccount>) => response.data.accCode),
        catchError(() => of('Account Not Found'))
      );

    default:
      return of('Unknown Account Type');
  }
}


  previousPage() {
    if (this.currentPage > 0) {
      this.loadTransactions(this.currentPage - 1);
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.loadTransactions(this.currentPage + 1);
    }
  }

  onCashTransactionsPageChange(page: number) {
    if (page >= 0 && page < this.cashTotalPages) {
      this.loadCashTransactions(page);
    }
  }

  // Pagination methods for users
  onUserPageChange(page: number) {
    if (page >= 0 && page < this.userTotalPages) {
      this.userCurrentPage = page;
      this.loadBranchUsers();
    }
  }

  // Pagination methods for clients
  onClientPageChange(page: number) {
    if (page >= 0 && page < this.clientTotalPages) {
      this.clientCurrentPage = page;
      this.loadBranchClients();
    }
  }

  get userPageNumbers(): number[] {
    return Array.from({ length: this.userTotalPages }, (_, i) => i);
  }

  get clientPageNumbers(): number[] {
    return Array.from({ length: this.clientTotalPages }, (_, i) => i);
  }

  getStatusBadgeClass(status: BranchStatus): string {
    switch (status) {
      case BranchStatus.ACTIVE:
        return 'bg-success';
      case BranchStatus.TERMINATED:
        return 'bg-danger';
      case BranchStatus.CLOSED:
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }

  getUserStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'terminated':
        return 'bg-danger';
      case 'closed':
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }

  getClientStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'terminated':
        return 'bg-danger';
      case 'closed':
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }

  refreshBalance() {
    if (this.refreshing || !this.branch) return;
    
    this.refreshing = true;
    this.branchService.getBranchAccount(this.branch.id!).subscribe({
      next: (response) => {
        this.branchAccount = response.data;
        this.refreshing = false;
      },
      error: (error) => {
        console.error('Error refreshing balance:', error);
        this.toastr.error('Failed to refresh balance');
        this.refreshing = false;
      }
    });
  }

  get activeUsers() {
    return this.users.filter(user => user.status?.toLowerCase() === 'active');
  }

  get activeClients() {
    return this.clients.filter(client => client.status?.toLowerCase() === 'active');
  }

  formatCIFType(type: CIFType): string {
    if (!type) return '';
    return type.split('_')
      .map(word => word.charAt(0) + word.slice(1).toLowerCase())
      .join(' ');
  }

  openUpdateModal() {
    if (!this.branch) return;

    const modalRef = this.modalService.open(BranchUpdateComponent, {
      size: 'lg',
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.branch = this.branch;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadBranchDetails(this.branch!.id!);
          this.toastr.success('Branch updated successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }

  changeStatus(newStatus: BranchStatus) {
    if (!this.branch || this.branch.status === newStatus) return;

    const modalRef = this.modalService.open(ConfirmationModalComponent, {
      centered: true
    });
    
    modalRef.componentInstance.title = 'Change Branch Status';
    modalRef.componentInstance.message = `Are you sure you want to change the branch's status to ${newStatus.toLowerCase()}?`;
    modalRef.componentInstance.confirmText = 'Change Status';
    modalRef.componentInstance.confirmButtonClass = 
      newStatus === BranchStatus.ACTIVE ? 'btn-success' : 
      newStatus === BranchStatus.TERMINATED ? 'btn-danger' : 'btn-warning';

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loading = true;
          this.loadingDelayService.addDelay(
            this.branchService.changeBranchStatus(this.branch!.id!, newStatus)
          ).subscribe({
            next: () => {
              setTimeout(() => {
                if (this.branch) {
                  this.branch = {
                    ...this.branch,
                    status: newStatus
                  };
                }
                this.loading = false;
                
                const statusMessage = {
                  [BranchStatus.ACTIVE]: 'Branch has been activated successfully',
                  [BranchStatus.TERMINATED]: 'Branch has been terminated successfully',
                  [BranchStatus.CLOSED]: 'Branch has been closed successfully'
                }[newStatus] || 'Status updated successfully';
                
                this.toastr.success(statusMessage, 'Status Change', {
                  timeOut: 3000,
                  closeButton: true,
                  progressBar: true
                });
              }, 500);
            },
            error: (error) => {
              this.error = 'Failed to change branch status';
              this.loading = false;
              console.error('Error changing status:', error);
              
              this.toastr.error(
                `Failed to change status to ${newStatus}`, 
                'Status Change Failed',
                {
                  timeOut: 4000,
                  closeButton: true,
                  progressBar: true
                }
              );
            }
          });
        }
      },
      () => {} // Modal dismissed
    );
  }

  openTransferModal() {
    if (!this.branch || !this.branchAccount) return;

    const modalRef = this.modalService.open(BranchTransferComponent, {
      size: 'lg',
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.branch = this.branch;
    modalRef.componentInstance.branchAccount = this.branchAccount;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadBranchAccount();
          this.toastr.success('Transfer completed successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }

  openCashInModal() {
    if (!this.branch || !this.branchAccount) return;

    const modalRef = this.modalService.open(BranchCashOperationComponent, {
      size: 'md',
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.branch = this.branch;
    modalRef.componentInstance.branchAccount = this.branchAccount;
    modalRef.componentInstance.operationType = CashInOutTransaction.Type.Cash_In;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadBranchAccount();
          this.toastr.success('Cash in completed successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }

  openCashOutModal() {
    if (!this.branch || !this.branchAccount) return;

    const modalRef = this.modalService.open(BranchCashOperationComponent, {
      size: 'md',
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.branch = this.branch;
    modalRef.componentInstance.branchAccount = this.branchAccount;
    modalRef.componentInstance.operationType = CashInOutTransaction.Type.Cash_Out;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadBranchAccount();
          this.toastr.success('Cash out completed successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }
}