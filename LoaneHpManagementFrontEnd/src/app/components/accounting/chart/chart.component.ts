import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  NgxChartsModule,
  PieChartComponent,
  LineChartComponent,
  Color,
  ScaleType
} from '@swimlane/ngx-charts';
import { CashInOutTransaction } from 'src/app/models/cash-in-out-transaction.model';
import { Transaction } from 'src/app/models/transaction.model';
import { TransactionService } from 'src/app/services/transaction.service';
import { CashInOutService } from 'src/app/services/cash-in-out.service';
import { AuthService } from 'src/app/services/auth.service';
import { BranchService } from 'src/app/services/branch.service';
import { BranchCurrentAccount } from 'src/app/models/branch-account.model';
import { Branch } from 'src/app/models/branch.model';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    NgxChartsModule,
   
  ]
})
export class ChartComponent implements OnInit {
  public transactionTypeData: any[] = [];
  public cashFlowData: any[] = [];
  view: [number, number] = [800, 400];
  public isLoading = true;
  public error: string | null = null;
  private branchAccount: BranchCurrentAccount | null = null;

  public colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5']
  };

  constructor(
    private transactionService: TransactionService,
    private cashService: CashInOutService,
    private authService: AuthService,
    private branchService: BranchService,
    
  ) {}

  async ngOnInit() {
    try {
      const user = await lastValueFrom(this.authService.getCurrentUser());
      
      if (!user?.branch?.id) {
        throw new Error('User branch information not found');
      }
  
      const branch = await lastValueFrom(this.branchService.getBranchById(user.branch.id));
      if (!branch?.id) throw new Error('Branch not found');
      
      // Add explicit check for branch.id
      if (typeof branch.id !== 'number') {
        throw new Error('Invalid branch ID format');
      }
  
      const accountResponse = await lastValueFrom(
        this.branchService.getBranchAccount(branch.id) // Now guaranteed to be number
      );
      
      if (!accountResponse?.data) throw new Error('Branch account not found');
      this.branchAccount = accountResponse.data;
      
      await this.loadTransactions();
      await this.loadCashTransactions();
      
    } catch (error) {
      this.error = error instanceof Error ? error.message : 'Failed to load data';
      console.error(error);
    } finally {
      this.isLoading = false;
    }
  }

  private async loadTransactions() {
    if (!this.branchAccount?.id) return;
    
    try {
      const response = await lastValueFrom(
        this.transactionService.getTransactionsByAccountId(
          this.branchAccount.id, 
          0, 
          1000
        )
      );
      
      if (response?.data?.content) {
        this.processTransactions(response.data.content);
      }
    } catch (error) {
      console.error('Error loading transactions:', error);
    }
  }

  private async loadCashTransactions() {
    if (!this.branchAccount?.id) return;
    
    try {
      const response = await lastValueFrom(
        this.cashService.getTransactionsByAccountId(
          this.branchAccount.id, 
          0, 
          1000
        )
      );
      
      if (response?.data?.content) {
        this.processCashTransactions(response.data.content);
      }
    } catch (error) {
      console.error('Error loading cash transactions:', error);
    }
  }

  private processTransactions(transactions: Transaction[]) {
    const amounts = transactions.reduce((acc: Record<string, number>, t) => {
      const type = t.paymentMethod?.paymentType || 'Unknown';
      acc[type] = (acc[type] || 0) + (t.amount || 0);
      return acc;
    }, {});

    this.transactionTypeData = Object.entries(amounts)
      .map(([name, value]) => ({ name, value }));
  }

  private processCashTransactions(transactions: CashInOutTransaction[]) {
    this.cashFlowData = [{
      name: 'Cash Flow',
      series: transactions
        .filter(t => t.transactionDate && !isNaN(new Date(t.transactionDate).getTime()))
        .map(t => ({
          name: new Date(t.transactionDate!),
          value: t.amount * (t.type === 'Cash_In' ? 1 : -1),
          extra: {
            description: t.description || 'No description',
            date: t.transactionDate ? 
              new Date(t.transactionDate).toLocaleDateString() : 
              'Date not available'
          }
        }))
    }];
  }

  public dateFormat(val: Date): string {
    return val.toLocaleDateString();
  }
}