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
    const params = {
      page: page.toString(),
      size: size.toString(),
      sortBy: sortBy
    };


    return this.http.get<ApiResponse<PagedResponse<DealerProduct>>>(`${this.apiUrl}/getAll`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  getAllProductToSelect(searchTerm: string = ''): Observable<DealerProduct[]> {
    const params = { searchTerm };
    return this.http.get<DealerProduct[]>(`${this.apiUrl}/getForSelect`, { params });
  }
} 