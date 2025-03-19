import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PaymentMethod, PagedResponse, PaymentMethodStatus } from '../models/payment-method.model';
import { ApiResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentMethodService {
  private apiUrl = `${environment.apiUrl}/payment-methods`;

  constructor(private http: HttpClient) {}

  getAllPaymentMethods(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<ApiResponse<PagedResponse<PaymentMethod>>> {
    return this.http.get<ApiResponse<PagedResponse<PaymentMethod>>>(
      `${this.apiUrl}/list?page=${page}&size=${size}&sortBy=${sortBy}`
    );
  }

  updateStatus(id: number, status: PaymentMethodStatus): Observable<ApiResponse<PaymentMethod>> {
    return this.http.put<ApiResponse<PaymentMethod>>(`${this.apiUrl}/${id}/status`, { status });
  }
  createPaymentMethod(method: Partial<PaymentMethod>): Observable<ApiResponse<PaymentMethod>> {
    return this.http.post<ApiResponse<PaymentMethod>>(`${this.apiUrl}`, method);
  }

  updatePaymentMethod(id: number, method: Partial<PaymentMethod>): Observable<ApiResponse<PaymentMethod>> {
    return this.http.put<ApiResponse<PaymentMethod>>(`${this.apiUrl}/${id}`, method);
  }

  
} 