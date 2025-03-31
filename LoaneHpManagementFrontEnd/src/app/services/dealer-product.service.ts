import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { DealerProduct } from '../models/dealer-product.model';
import { ApiResponse, PagedResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class DealerProductService {
  private apiUrl = `${environment.apiUrl}/dealer-products`;

  constructor(private http: HttpClient) {}

  getAllDealerProducts(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<PagedResponse<DealerProduct>> {
    const params = { page: page.toString(), size: size.toString(), sortBy };
    return this.http.get<ApiResponse<PagedResponse<DealerProduct>>>(`${this.apiUrl}/getAll`, { params })
      .pipe(map(response => response.data));
  }

  getAllProductToSelect(searchTerm: string = ''): Observable<DealerProduct[]> {
    const params = { searchTerm };
    return this.http.get<DealerProduct[]>(`${this.apiUrl}/getForSelect`, { params });
  }

  getDealerProductById(id: number): Observable<DealerProduct> {
    return this.http.get<ApiResponse<DealerProduct>>(`${this.apiUrl}/list/${id}`)
      .pipe(map(response => response.data));
  }

  getDealerProductsByCifId(cifId: number): Observable<DealerProduct[]> {
    return this.http.get<ApiResponse<DealerProduct[]>>(`${this.apiUrl}/cif/${cifId}`)
      .pipe(map(response => response.data));
  }

  createDealerProduct(cifId: number, dealerProduct: DealerProduct): Observable<DealerProduct> {
    return this.http.post<ApiResponse<DealerProduct>>(`${this.apiUrl}/save/${cifId}`, dealerProduct)
      .pipe(map(response => response.data));
  }
 // In dealer-product.service.ts
// In dealer-product.service.ts
updateDealerProductStatus(id: number, status: string) {
  return this.http.put<any>(
    `${this.apiUrl}/${id}/status`, 
    { status }
  );
}

  updateDealerProduct(id: number, dealerProduct: DealerProduct): Observable<DealerProduct> {
    return this.http.put<ApiResponse<DealerProduct>>(`${this.apiUrl}/update/${id}`, dealerProduct)
      .pipe(map(response => response.data));
  }

  deleteDealerProduct(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/delete/${id}`)
      .pipe(map(() => undefined));
  }
} 