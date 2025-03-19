import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Company } from '../models/company.model';
import { BusinessPhoto } from '../models/business-photo.model';
import { ApiResponse, PagedResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private apiUrl = `${environment.apiUrl}/companies`;

  constructor(private http: HttpClient) {}

  getCompaniesByCif(cifId: number, page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<PagedResponse<Company>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<ApiResponse<PagedResponse<Company>>>(`${this.apiUrl}/byCif/${cifId}`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  createCompany(companyData: any): Observable<Company> {
    return this.http.post<ApiResponse<Company>>(`${this.apiUrl}/save`, companyData)
      .pipe(
        map(response => response.data)
      );
  }

  getBusinessPhotosByCompany(companyId: number): Observable<BusinessPhoto[]> {
    return this.http.get<ApiResponse<BusinessPhoto[]>>(`${this.apiUrl}/byCompany/${companyId}`)
      .pipe(
        map(response => response.data)
      );
  }

  updateCompany(id: number, companyData: Partial<Company>): Observable<Company> {
    return this.http.put<ApiResponse<Company>>(`${this.apiUrl}/update/${id}`, companyData)
      .pipe(
        map(response => response.data)
      );
  }
} 