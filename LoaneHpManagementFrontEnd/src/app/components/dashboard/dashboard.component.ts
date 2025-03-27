import { Component } from '@angular/core';
import { DashboardService, DashboardStats } from 'src/app/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  stats!: DashboardStats;
  isLoading = true;
  loanStatusData: any[] = [];
branchBalanceData: any[] = [];
colorScheme = {
  domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
};

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading dashboard:', err);
        this.isLoading = false;
      }
    });
    this.loadCharts();
  }
  private loadCharts() {
    // Loan Status Chart
    this.dashboardService.getLoanStatus().subscribe(data => {
      this.loanStatusData = this.transformLoanData(data);
    });
  
    // Branch Balances
   // Change this in loadCharts()
this.dashboardService.getBranchBalances().subscribe(data => {
  this.branchBalanceData = data.map(item => ({
    name: item.branchName,  // Changed from item[0]
    value: item.balance     // Changed from item[1]
  }));
});
  }
  
  private transformLoanData(rawData: any[]): any[] {
    const years = [...new Set(rawData.map(item => item.year))];
    return years.map(year => ({
      name: year.toString(),
      series: rawData
        .filter(item => item.year === year)
        .map(item => ({
          name: `${item.status} (${item.loanType})`,
          value: item.count
        }))
    }));
  }
  getMaxBalance(): number {
    if (!this.branchBalanceData.length) return 100;
    return Math.max(...this.branchBalanceData.map(item => item.value)) * 1.1;
  }
}
