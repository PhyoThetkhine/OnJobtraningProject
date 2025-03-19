import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { HpLoan, HpLoanResponse, CreateHpLoanDto, HpTerm } from '../models/hp-loan.model';
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
}

@Injectable({
  providedIn: 'root'
})
export class HpLoanService {
  private apiUrl = `${environment.apiUrl}/HPLoan`;

  constructor(private http: HttpClient) {}

  getLoans(page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<PagedResponse<HpLoan>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<HpLoanResponse>(`${this.apiUrl}/all`, { params })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  getLoanById(id: number): Observable<HpLoan> {
    return this.http.get<ApiResponse<HpLoan>>(`${this.apiUrl}/getBy/${id}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  getTermsByLoanId(loanId: number): Observable<HpTerm[]> {
    return this.http.get<HpTerm[]>(`${this.apiUrl}/terms/${loanId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  createLoan(loanData: CreateHpLoanDto): Observable<HpLoan> {
    console.log('Creating loan with data:', loanData);
    return this.http.post<ApiResponse<HpLoan>>(`${this.apiUrl}/create`, loanData)
      .pipe(
        tap(response => console.log('Server response:', response)),
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  updateLoan(id: number, loan: Partial<HpLoan>): Observable<HpLoan> {
    return this.http.put<ApiResponse<HpLoan>>(`${this.apiUrl}/update/${id}`, loan)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  updateStatus(id: number, status: string): Observable<HpLoan> {
    console.log(`Changing status for loan ${id} to ${status}`);
    return this.http.put<ApiResponse<HpLoan>>(`${this.apiUrl}/status/${id}`, { status })
      .pipe(
        tap(response => console.log('Status change response:', response)),
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  confirmLoan(loanId: number, confirmData: ConfirmLoanData): Observable<any> {
    return this.http.post(`${this.apiUrl}/confirm/${loanId}`, confirmData);
  }

  getHPLoansByCif(cifId: number, page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<PagedResponse<HpLoan>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<ApiResponse<PagedResponse<HpLoan>>>(`${this.apiUrl}/byCif/${cifId}`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      errorMessage = error.error?.message || `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 