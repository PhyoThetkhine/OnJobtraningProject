// transactions.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BranchService } from '../../../services/branch.service';
import { TransactionService } from '../../../services/transaction.service';
import { CashInOutService } from '../../../services/cash-in-out.service';
import { AuthService } from '../../../services/auth.service';
import { Transaction } from '../../../models/transaction.model';
import { CashInOutTransaction } from '../../../models/cash-in-out-transaction.model';
import { BranchCurrentAccount } from '../../../models/branch-account.model';
import { catchError, map, Observable, of, shareReplay } from 'rxjs';
import { CIFCurrentAccount } from 'src/app/models/cif-current-account.model';
import { ApiResponse } from 'src/app/models/user.model';
import { CIFCurrentAccountService } from 'src/app/services/cif-current-account.service';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class TransactionsComponent implements OnInit {
  loading = true;
  error: string | null = null;
  branchAccount: BranchCurrentAccount | null = null;
  
  // Transaction properties
  transactionLoading = false;
  transactions: Transaction[] = [];
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Cash transactions properties
  cashTransactionsLoading = false;
  cashTransactions: CashInOutTransaction[] = [];
  cashCurrentPage = 0;
  cashPageSize = 10;
  cashTotalElements = 0;
  cashTotalPages = 0;
  protected Math = Math;

  constructor(
    private authService: AuthService,
    private branchService: BranchService,
    private transactionService: TransactionService,
    private cashService: CashInOutService,
    private toastr: ToastrService,
    private cifService:CIFCurrentAccountService,
  ) {}

  async ngOnInit() {
    try {
      const user = await this.authService.getCurrentUser().toPromise();
      if (!user?.branch?.id) throw new Error('User branch information not found');

      const branch = await this.branchService.getBranchById(user.branch.id).toPromise();
      if (!branch) throw new Error('Branch not found');

      const accountResponse = await this.branchService.getBranchAccount(branch.id!).toPromise();
      if (!accountResponse?.data) throw new Error('Branch account not found');
      
      this.branchAccount = accountResponse.data;
      await this.loadTransactions();
      await this.loadCashTransactions();

    } catch (error) {
      this.error = error instanceof Error ? error.message : 'Failed to load data';
      this.toastr.error(this.error);
    } finally {
      this.loading = false;
    }
  }

  private async loadTransactions(page: number = 0) {
    if (!this.branchAccount?.id) return;

    this.transactionLoading = true;
    try {
      const response = await this.transactionService.getTransactionsByAccountId(
        this.branchAccount.id,
        page,
        this.pageSize
      ).toPromise();

      if (response?.data) {
        this.transactions = response.data.content;
        this.totalElements = response.data.totalElements;
        this.totalPages = response.data.totalPages;
        this.currentPage = response.data.number;
      }
    } catch (error) {
      this.toastr.error('Failed to load transactions');
    } finally {
      this.transactionLoading = false;
    }
  }

  private async loadCashTransactions(page: number = 0) {
    if (!this.branchAccount?.id) return;

    this.cashTransactionsLoading = true;
    try {
      const response = await this.cashService.getTransactionsByAccountId(
        this.branchAccount.id,
        page,
        this.cashPageSize
      ).toPromise();

      if (response?.data) {
        this.cashTransactions = response.data.content;
        this.cashTotalElements = response.data.totalElements;
        this.cashTotalPages = response.data.totalPages;
        this.cashCurrentPage = response.data.number;
      }
    } catch (error) {
      this.toastr.error('Failed to load cash transactions');
    } finally {
      this.cashTransactionsLoading = false;
    }
  }

  // Pagination methods
  onTransactionPageChange(newPage: number) {
    this.currentPage = newPage;
    this.loadTransactions(newPage);
  }
  
  
  // Updated method name to match template
  onCashTransactionsPageChange(newPage: number) {
    this.cashCurrentPage = newPage;
    this.loadCashTransactions(newPage);
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
        return this.cifService.getAccountByCifId(accountId).pipe(
          map((response: CIFCurrentAccount) => response.accCode),
          catchError(() => of('CIF Not Found'))
        );
  
      case 'BRANCH':
        return this.branchService.getBranchAccount(accountId).pipe(
          map((response: ApiResponse<BranchCurrentAccount>) => response.data.accCode),
          catchError(() => of('Branch Not Found'))
        );
  
      default:
        return of('Unknown Account Type');
    }
  }

  
}