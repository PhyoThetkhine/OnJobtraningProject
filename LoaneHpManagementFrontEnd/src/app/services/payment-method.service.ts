import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { PaymentMethod, PagedResponse, PaymentMethodStatus } from '../models/payment-method.model';
import { ApiResponse, CurrentUser } from '../models/user.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PaymentMethodService {
  private apiUrl = `${environment.apiUrl}/payment-methods`;

  constructor(private http: HttpClient,
    private authService: AuthService 
  ) {}

  getAllPaymentMethods(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<ApiResponse<PagedResponse<PaymentMethod>>> {
    return this.http.get<ApiResponse<PagedResponse<PaymentMethod>>>(
      `${this.apiUrl}/list?page=${page}&size=${size}&sortBy=${sortBy}`
    );
  }
  deletePaymentMethod(id: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      `${this.apiUrl}/${id}/status/deleted`,
      {}, // Empty body for PATCH
    { responseType: 'json' as 'json' } 
     
    );
  }
  
  // For reactivation
  reactivatePaymentMethod(id: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      `${this.apiUrl}/${id}/status/active`,
      {}, // Empty body for PATCH
    { responseType: 'json' as 'json' } 
     
    );
  }

 
  createPaymentMethod(method: Partial<PaymentMethod>): Observable<ApiResponse<PaymentMethod>> {
    console.log("method " + method.id);
    console.log("method " + method.paymentType);
    console.log("method " + method.status);
    console.log("method " + method.updatedDate);
    return this.authService.getCurrentUser().pipe(
      switchMap((user: CurrentUser) => {
        const userId = user.id
        ;
      
        return this.http.post<ApiResponse<PaymentMethod>>(`${this.apiUrl}/create/${userId}`, method);
      })
    );
  }
  updatePaymentMethod(id: number, method: Partial<PaymentMethod>): Observable<ApiResponse<PaymentMethod>> {
    return this.http.put<ApiResponse<PaymentMethod>>(`${this.apiUrl}/update/${id}`, method);
  }

  
} 