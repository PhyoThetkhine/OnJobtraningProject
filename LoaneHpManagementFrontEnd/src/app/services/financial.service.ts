import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Financial } from '../models/financial.model';
import { ApiResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class FinancialService {
  private apiUrl = `${environment.apiUrl}/financials`;

  constructor(private http: HttpClient) {}

  getFinancialByCompany(companyId: number): Observable<Financial> {
    return this.http.get<ApiResponse<Financial>>(`${this.apiUrl}/company/${companyId}`)
      .pipe(
        map(response => response.data)
      );
  }

  updateFinancial(id: number, financialData: Partial<Financial>): Observable<Financial> {
    return this.http.put<ApiResponse<Financial>>(`${this.apiUrl}/update/${id}`, financialData)
      .pipe(
        map(response => response.data)
      );
  }
} 