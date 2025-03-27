// dashboard.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface DashboardStats {
  totalCif: number;
  totalUsers: number;
  totalLoans: number;
  totalBranchAccounts: number;
  activeLoans: number;
  totalLoanAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
     private apiUrl = `${environment.apiUrl}/dashboard`;
  

  constructor(private http: HttpClient) { }

  getStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(this.apiUrl);
  }
  
  getLoanStatus(): Observable<any[]> {
    return this.http.get<any[]>( `${this.apiUrl}/loan-status`);
  }
  
  getBranchBalances(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/branch-balances`);
  }
}