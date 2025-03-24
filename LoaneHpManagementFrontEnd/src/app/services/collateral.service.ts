import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Collateral } from '../models/collateral.model';
import { map } from 'rxjs/operators';
import { ApiResponse, PagedResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class CollateralService {
  private apiUrl = `${environment.apiUrl}/collaterals`;

  constructor(private http: HttpClient) {}

  getCollateralsByLoanId(
    loanId: number, 
    page: number = 0, 
    size: number = 10, 
    sortBy: string = 'id'
  ): Observable<PagedResponse<Collateral>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<ApiResponse<PagedResponse<Collateral>>>(`${this.apiUrl}/loan/${loanId}`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  getCollateralsByCifId(
    cifId: number, 
    page: number = 0, 
    size: number = 10, 
    sortBy: string = 'id'
  ): Observable<PagedResponse<Collateral>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<ApiResponse<PagedResponse<Collateral>>>(`${this.apiUrl}/cif/${cifId}`, { params })
      .pipe(
        map(response => response.data)
      );
  }
  getCollateralsByCifIdToSelect(cifId: number): Observable<Collateral[]> {
    return this.http.get<ApiResponse<Collateral[]>>(`${this.apiUrl}/cif/${cifId}/toSelect`)
      .pipe(
        map(response => response.data)
      );
  }

  createCollateral(collateral: Partial<Collateral>): Observable<Collateral> {
    return this.http.post<ApiResponse<Collateral>>(`${this.apiUrl}/create`, collateral)
      .pipe(
        map(response => response.data)
      );
  }

  updateCollateral(id: number, collateral: Partial<Collateral>): Observable<Collateral> {
    return this.http.put<ApiResponse<Collateral>>(`${this.apiUrl}/update/${id}`, collateral)
      .pipe(
        map(response => response.data)
      );
  }

  deleteCollateral(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status !== 200) {
            throw new Error(response.message || 'Error deleting collateral');
          }
        })
      );
  }
} 