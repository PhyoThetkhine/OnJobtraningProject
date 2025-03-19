import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { MainCategory } from '../models/main-category.model';
import { ApiResponse, PagedResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class MainCategoryService {
  private apiUrl = `${environment.apiUrl}/mainCategory`;

  constructor(private http: HttpClient) {}

  getAllMainCategories(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<PagedResponse<MainCategory>> {
    const params = {
      page: page.toString(),
      size: size.toString(),
      sortBy: sortBy
    };

    return this.http.get<ApiResponse<PagedResponse<MainCategory>>>(`${this.apiUrl}/getAll`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  createMainCategory(category: string): Observable<MainCategory> {
    return this.http.post<ApiResponse<MainCategory>>(`${this.apiUrl}/create`, { category })
      .pipe(
        map(response => response.data)
      );
  }

  updateMainCategory(id: number, category: string): Observable<MainCategory> {
    return this.http.put<ApiResponse<MainCategory>>(`${this.apiUrl}/update/${id}`, { category })
      .pipe(
        map(response => response.data)
      );
  }
} 