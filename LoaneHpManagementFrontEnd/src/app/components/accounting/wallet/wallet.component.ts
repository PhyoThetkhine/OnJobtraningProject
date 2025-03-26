import { Component, OnInit } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BranchService } from '../../../services/branch.service';
import { Branch, BranchStatus } from '../../../models/branch.model';
import { ApiResponse, User } from '../../../models/user.model';
import { CIF } from '../../../models/cif.model';
import { BranchCurrentAccount } from '../../../models/branch-account.model';
import { LoadingDelayService } from '../../../services/loading-delay.service';
import { BranchTransferComponent } from '../../branch/branch-transfer/branch-transfer.component';
import { BranchCashOperationComponent } from '../../branch/branch-cash-operation/branch-cash-operation.component';
import { CashInOutTransaction } from '../../../models/cash-in-out-transaction.model';
import { TransactionService } from '../../../services/transaction.service';
import { CashInOutService } from '../../../services/cash-in-out.service';
import { AccountType, Transaction } from '../../../models/transaction.model';
import { AuthService } from '../../../services/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CIFCurrentAccountService } from 'src/app/services/cif-current-account.service';
import { CIFCurrentAccount } from 'src/app/models/cif-current-account.model';
import { catchError, map, Observable, of, shareReplay } from 'rxjs';
import { ChartComponent } from '../chart/chart.component';


@Component({
  selector: 'app-wallet',
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NgxChartsModule,
    ChartComponent
]
})
export class WalletComponent implements OnInit {
  branch: Branch | null = null;
  users: User[] = [];
  clients: CIF[] = [];
  loading = false;
  error: string | null = null;
  activeTab = 'members';

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

  // Properties for regular transactions
  transactionLoading = false;
  transactions: Transaction[] = [];
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Properties for cash transactions
  cashTransactionsLoading = false;
  cashTransactions: CashInOutTransaction[] = [];
  cashCurrentPage = 0;
  cashPageSize = 10;
  cashTotalElements = 0;
  cashTotalPages = 0;
  public transactionTypeData: any[] = [];
  public width: number = 800;
  public height: number = 400;

  constructor(
    private branchService: BranchService,
    private toastr: ToastrService,
    private loadingDelayService: LoadingDelayService,
    private transactionService: TransactionService,
    private cashInOutService: CashInOutService,
    private authService: AuthService,
    private modalService: NgbModal,
    private cifService:CIFCurrentAccountService,
  ) {}

  ngOnInit() {
    this.loadCurrentUserBranch();
    this.loadTransactions(0);
    this.loadCashTransactions(0);
  }

  loadCurrentUserBranch() {
    this.loading = true;
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (user && user.branch?.id) {
          this.loadBranchDetails(user.branch.id);
        } else {
          this.error = 'No branch associated with current user';
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Error loading current user:', error);
        this.toastr.error('Failed to load user details');
        this.loading = false;
      }
    });
  }

  loadBranchDetails(branchId: number) {
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
  isDebit(transaction: Transaction): boolean {
    return transaction.fromAccountId === this.branchAccount?.id && 
           transaction.fromAccountType === AccountType.BRANCH;
  }
  
  isCredit(transaction: Transaction): boolean {
    return transaction.toAccountId === this.branchAccount?.id && 
           transaction.toAccountType === AccountType.BRANCH;
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
    
    if ((this.activeTab === 'transactions' || this.activeTab === 'balance'||this.activeTab == 'members') && this.branch) {
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
          this.processTransactionTypes();
        this.processCashFlow();
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
  

  loadCashTransactions(page: number) {
    if (!this.branchAccount?.id) return;
    
    this.cashTransactionsLoading = true;
    this.cashInOutService.getTransactionsByAccountId(this.branchAccount.id, page, this.cashPageSize)
      .subscribe({
        next: (response) => {
          if (response && response.data) {
            this.cashTransactions = response.data.content;
           
            this.processTransactionTypes();
        this.processCashFlow();
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

  onUserPageChange(page: number) {
    if (page >= 0 && page < this.userTotalPages) {
      this.userCurrentPage = page;
      this.loadBranchUsers();
    }
  }

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
    this.loadingDelayService.addDelay(
      this.branchService.getBranchAccount(this.branch.id!)
    ).subscribe({
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
          this.loadCashTransactions(0);
          this.loadTransactions(0);
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
          this.loadCashTransactions(0);
          this.loadTransactions(0);
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
    modalRef.componentInstance.transactionData = {
      branchCurrentAccount: this.branchAccount,
      type: CashInOutTransaction.Type.Cash_Out,
      amount: 0,
      description: ''
    };

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadBranchAccount();
          this.loadCashTransactions(0);
          this.loadTransactions(0);
          this.toastr.success('Cash out completed successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }
// In your component
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
  console.log("account id:"+accountId)
  console.log("accountType:"+accountType)
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
public hasPermission(permission: string): boolean {
  return this.authService.hasPermission(permission);
}
  // Add cash flow chart data property
  cashFlowData: any[] = [];
  colorScheme = { domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5'] };
  dateFormat = (d: Date) => d.toLocaleDateString();
  amountFormat = (value: number) => `$${value.toLocaleString()}`;

  private processTransactionTypes() {
    const typeCounts = this.transactions.reduce((acc, transaction) => {
      const type = transaction.paymentMethod.paymentType; // Or use transaction.type if available
      acc[type] = (acc[type] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);
  
    this.transactionTypeData = Object.entries(typeCounts).map(([name, value]) => ({
      name,
      value
    }));
  }

  // Add cash flow processing method
  private processCashFlow() {
    const transactionsToUse = this.cashTransactions;
  
    this.cashFlowData = [{
      name: 'Cash Flow',
      series: transactionsToUse
        .map(transaction => {
          // Validate transaction date
          const rawDate = transaction.transactionDate;
          console.log(rawDate)
          if (!rawDate) {
            console.warn('Missing transaction date:', transaction);
            return null;
          }
          
          const dateValue = new Date(rawDate);
          if (isNaN(dateValue.getTime())) {
            console.warn('Invalid transaction date:', rawDate);
            return null;
          }
  
          // Validate and parse amount
          const amount = Number(transaction.amount);
          if (isNaN(amount) || amount <= 0) {
            console.warn('Invalid transaction amount:', transaction.amount);
            return null;
          }
  
          return {
            name: dateValue,
            value: transaction.type === CashInOutTransaction.Type.Cash_In ? amount : -amount,
            extra: {
              description: transaction.description,
              date: dateValue.toLocaleDateString()
            }
          };
        })
        .filter(item => item !== null)
    }];
  }
}
