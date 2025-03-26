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
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule],
  providers: [DatePipe]
})
export class TransactionsComponent implements OnInit {
  loading = true;
  error: string | null = null;
  branchAccount: BranchCurrentAccount | null = null;

  private resolvedAccountCodes = new Map<string, string>();
  private exportPending = false;
private exportDataReady = new Map<string, boolean>();
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
    private datePipe: DatePipe
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
        return this.cifService.getAccountById(accountId).pipe(
          map((response: CIFCurrentAccount) => response.accCode),
          catchError(() => of('CIF Not Found'))
        );
  
      case 'BRANCH':
        return this.branchService.getBranchAccountById(accountId).pipe(
          map((response: ApiResponse<BranchCurrentAccount>) => response.data.accCode),
          catchError(() => of('account Not Found'))
        );
  
      default:
        return of('Unknown Account Type');
    }
  }
// Add this new method to preload account codes
private async preloadAccountCodes(transactions: any[]) {
  const promises: Promise<void>[] = [];
  const seen = new Set<string>();

  transactions.forEach(transaction => {
    // For outgoing transactions
    if (transaction.fromAccountId === this.branchAccount?.id) {
      const cacheKey = `${transaction.toAccountType}_${transaction.toAccountId}`;
     if (!seen.has(cacheKey)){
        seen.add(cacheKey);
        promises.push(this.cacheAccountCode(transaction.toAccountId, transaction.toAccountType));
      }
    }
    
    // For incoming transactions
    if (transaction.toAccountId === this.branchAccount?.id) {
      const cacheKey = `${transaction.fromAccountType}_${transaction.fromAccountId}`;
      if (!seen.has(cacheKey)) {
        seen.add(cacheKey);
        promises.push(this.cacheAccountCode(transaction.fromAccountId, transaction.fromAccountType));
      }
    }
  });

  await Promise.all(promises);
}
  async exportTransactions(type: 'pdf' | 'excel', section: 'transfers' | 'cash') {
    if (this.exportPending) return;
    
    try {
      this.exportPending = true;
      const data = section === 'transfers' ? this.transactions : this.cashTransactions;
      
      if (data.length === 0) {
        this.toastr.warning('No data to export');
        return;
      }
  
      // Preload all required account codes
      if (section === 'transfers') {
        await this.preloadAccountCodes(data);
      }
  
      const fileName = `${section}-transactions-${new Date().toISOString().slice(0,10)}`;
      
      if (type === 'pdf') {
        this.exportToPDF(data, fileName, section);
      } else {
        this.exportToExcel(data, fileName, section);
      }
    } catch (error) {
      this.toastr.error('Failed to prepare export data');
    } finally {
      this.exportPending = false;
    }
  }
  // Add this helper method to cache account codes
private async cacheAccountCode(accountId: number, accountType: string): Promise<void> {
  return new Promise((resolve) => {
    this.getAccountCode(accountId, accountType).subscribe({
      next: (code) => {
        const cacheKey = `${accountType.toUpperCase()}_${accountId}`;
        this.resolvedAccountCodes.set(cacheKey, code);
        resolve();
      },
      error: () => resolve()
    });
  });
}
private getFormattedAccountCode(accountId: number, accountType: string): string {
  const cacheKey = `${accountType.toUpperCase()}_${accountId}`;
  return this.resolvedAccountCodes.get(cacheKey) || 'N/A'; // Show N/A instead of Loading
}

  private exportToPDF(data: any[], fileName: string, section: string) {
    const doc = new jsPDF();
    const columns = this.getPDFColumns(section);
    const rows = this.getPDFRows(data, section);
  
    doc.setFontSize(18);
    doc.text(`${section.toUpperCase()} Transactions Report`, 14, 22);
    doc.setFontSize(11);
    doc.setTextColor(100);
  
    autoTable(doc, {
      head: [columns],
      body: rows,
      theme: 'striped',
      startY: 30,
      styles: { fontSize: 9 },
      headStyles: { fillColor: [41, 128, 185], textColor: 255 },
      columnStyles: { 0: { cellWidth: 30 } }
    });
  
    doc.save(`${fileName}.pdf`);
  }
  
  private exportToExcel(data: any[], fileName: string, section: string) {
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(
      data.map(item => this.formatExcelRow(item, section))
    );
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Transactions');
    XLSX.writeFile(wb, `${fileName}.xlsx`);
  }
  
  private getPDFColumns(section: string): string[] {
    return section === 'transfers' ? 
      ['Date', 'Type', 'From/To', 'Payment Method', 'Amount'] : 
      ['Date', 'Type', 'Description', 'Amount'];
  }
  
  private getPDFRows(data: any[], section: string): any[] {
    return data.map(item => {
      const date = this.datePipe.transform(item.transactionDate, 'medium');
      const amount = `${Number(item.amount).toLocaleString()} MMK`;
  
      if (section === 'transfers') {
        const direction = this.getTransactionDirection(item);
        return [
          date,
          item.fromAccountId === this.branchAccount?.id ? 'Debit' : 'Credit',
          direction,
          item.paymentMethod?.paymentType,
          amount
        ];
      }
      
      return [
        date,
        item.type === 'Cash_In' ? 'Cash In' : 'Cash Out',
        item.description,
        amount
      ];
    });
  }
  
  private formatExcelRow(item: any, section: string): any {
    const base = {
      Date: this.datePipe.transform(item.transactionDate, 'medium'),
      Amount: `${Number(item.amount).toLocaleString()} MMK`
    };
  
    if (section === 'transfers') {
      return {
        ...base,
        Type: item.fromAccountId === this.branchAccount?.id ? 'Debit' : 'Credit',
        'From/To': this.getTransactionDirection(item),
        'Payment Method': item.paymentMethod?.paymentType
      };
    }
  
    return {
      ...base,
      Type: item.type === 'Cash_In' ? 'Cash In' : 'Cash Out',
      Description: item.description
    };
  }
  private getTransactionDirection(transaction: any): string {
    if (transaction.fromAccountId === this.branchAccount?.id) {
      const toCode = this.getFormattedAccountCode(transaction.toAccountId, transaction.toAccountType);
      return `To: ${toCode}`;
    }
    if (transaction.toAccountId === this.branchAccount?.id) {
      const fromCode = this.getFormattedAccountCode(transaction.fromAccountId, transaction.fromAccountType);
      return `From: ${fromCode}`;
    }
    return 'External Transaction';
  }
  
}