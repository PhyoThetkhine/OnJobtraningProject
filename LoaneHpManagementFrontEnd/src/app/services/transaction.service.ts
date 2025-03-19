import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Transaction, TransactionResponse } from '../models/transaction.model';
import { ApiResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getUserTransactions(userId: number, page: number, pageSize: number, sortBy: string = 'transactionDate'): Observable<TransactionResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString())
      .set('sortBy', sortBy);
    return this.http.get<TransactionResponse>(`${this.apiUrl}/user/${userId}`, { params });
  }

  getTransactionsByCifId(cifId: number, page: number, pageSize: number, sortBy: string = 'transactionDate'): Observable<TransactionResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString())
      .set('sortBy', sortBy);
    return this.http.get<TransactionResponse>(`${this.apiUrl}/cif/${cifId}`, { params });
  }

  getTransactionsByAccountId(accountId: number, page: number, pageSize: number, sortBy: string = 'transactionDate'):Observable<ApiResponse<{ content: Transaction[], totalElements: number, totalPages: number, number: number, size: number }>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString())
      .set('sortBy', sortBy);
    return this.http.get<ApiResponse<{ content: Transaction[], totalElements: number, totalPages: number, number: number, size: number }>>(`${this.apiUrl}/account/${accountId}`, { params });
  }
 

  createTransaction(transaction: any): Observable<Transaction> {
    return this.http.post<ApiResponse<Transaction>>(
      `${this.apiUrl}/transfer`,
      transaction
    ).pipe(
      map(response => response.data)
    );
  }
}