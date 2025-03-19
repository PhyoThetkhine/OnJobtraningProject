import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { SMELoan, SMETerm } from '../models/sme-loan.model';
import { ApiResponse, PagedResponse } from '../models/common.types';

interface ConfirmLoanData {
  disbursementAmount: number;
  documentFeeRate: number;
  serviceChargeRate: number;
  gracePeriod: number;
  interestRate: number;
  lateFeeRate: number;
  defaultRate: number;
  longTermOverdueRate: number;
  confirmUserId: number;
  paidPrincipalStatus: 'paid' | 'not_paid';
}

@Injectable({
  providedIn: 'root'
})
export class SmeLoanService {
  private apiUrl = `${environment.apiUrl}/smeLoan`;

  constructor(private http: HttpClient) {}

  getAllLoans(page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<PagedResponse<SMELoan>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<ApiResponse<PagedResponse<SMELoan>>>(`${this.apiUrl}/all`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  getLoanById(id: number): Observable<SMELoan> {
    return this.http.get<ApiResponse<SMELoan>>(`${this.apiUrl}/getBy/${id}`)
      .pipe(
        map(response => response.data)
      );
  }

  createLoan(loan: Partial<SMELoan>): Observable<SMELoan> {
    return this.http.post<ApiResponse<SMELoan>>(`${this.apiUrl}/create`, loan)
      .pipe(
        map(response => response.data)
      );
  }

  updateLoan(id: number, loan: Partial<SMELoan>): Observable<SMELoan> {
    return this.http.put<ApiResponse<SMELoan>>(`${this.apiUrl}/update/${id}`, loan)
      .pipe(
        map(response => response.data)
      );
  }

  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  confirmLoan(id: number, confirmData: ConfirmLoanData): Observable<string> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/confirm/${id}`, confirmData)
      .pipe(
        map(response => response.data)
      );
  }

  getTermsByLoanId(loanId: number): Observable<SMETerm[]> {
    return this.http.get<SMETerm[]>(`http://localhost:8080/api/smeLoan/terms/${loanId}`);
  }

  updatePrincipalStatus(loanId: number, status: 'paid' | 'not_paid'): Observable<string> {
    const params = new HttpParams().set('status', status);
    return this.http.put<ApiResponse<string>>(`${this.apiUrl}/principalStatus/${loanId}`, null, { params })
      .pipe(
        map(response => response.data)
      );
  }

  getSMELoansByCif(cifId: number, page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<PagedResponse<SMELoan>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<ApiResponse<PagedResponse<SMELoan>>>(`${this.apiUrl}/byCif/${cifId}`, { params })
      .pipe(
        map(response => response.data)
      );
  }
} 