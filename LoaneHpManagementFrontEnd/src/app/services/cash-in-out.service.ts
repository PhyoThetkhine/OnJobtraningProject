import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { CashInOutTransaction } from '../models/cash-in-out-transaction.model';
import { ApiResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})

export class CashInOutService {
  private apiUrl = `${environment.apiUrl}/cash-transactions`;

  constructor(private http: HttpClient) {}

  createTransaction(transaction: Partial<CashInOutTransaction>): Observable<CashInOutTransaction> {
    return this.http.post<ApiResponse<CashInOutTransaction>>(`${this.apiUrl}/transfer`, transaction)
      .pipe(
        map(response => response.data)
      );
  }

  getTransactionsByAccountId(accountId: number, page: number = 0, size: number = 10): Observable<ApiResponse<{ content: CashInOutTransaction[], totalElements: number, totalPages: number, number: number, size: number }>> {
    return this.http.get<ApiResponse<{ content: CashInOutTransaction[], totalElements: number, totalPages: number, number: number, size: number }>>(
      `${this.apiUrl}/account/${accountId}?page=${page}&size=${size}`
    );
  }
}