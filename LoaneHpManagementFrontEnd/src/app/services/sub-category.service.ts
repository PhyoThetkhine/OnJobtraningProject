import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { SubCategory } from '../models/sub-category.model';
import { ApiResponse, PagedResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class SubCategoryService {
  private apiUrl = `${environment.apiUrl}/subCategory`;

  constructor(private http: HttpClient) {}

  getAllSubCategories(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<PagedResponse<SubCategory>> {
    const params = {
      page: page.toString(),
      size: size.toString(),
      sortBy: sortBy
    };

    return this.http.get<ApiResponse<PagedResponse<SubCategory>>>(`${this.apiUrl}/getAll`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  createSubCategory(category: string, mainCategoryId: number): Observable<SubCategory> {
    return this.http.post<ApiResponse<SubCategory>>(`${this.apiUrl}/create`, { 
      category,
      mainCategoryId
    }).pipe(
      map(response => response.data)
    );
  }

  updateSubCategory(id: number, category: string, mainCategoryId: number): Observable<SubCategory> {
    return this.http.put<ApiResponse<SubCategory>>(`${this.apiUrl}/update/${id}`, {
      category,
      mainCategoryId
    }).pipe(
      map(response => response.data)
    );
  }
} 