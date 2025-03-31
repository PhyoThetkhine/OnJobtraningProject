// src/app/services/report.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // Ensures it’s available app-wide
})
export class ReportService {
  private apiUrl: string = 'http://localhost:8080/api/reports';
  private baseUrl = '/api/reports';

  constructor(private http: HttpClient) {}

  getAllUsersPdfReport(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/users/pdf`, { responseType: 'blob' });
  }

  getUsersPdfReportByStatus(status?: string): Observable<Blob> {
    const url = status
      ? `${this.apiUrl}/users/pdf?status=${status}`
      : `${this.apiUrl}/users/pdf`;
    return this.http.get(url, { responseType: 'blob' });
  }

  getAllUsersExcelReport(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/users/excel`, { responseType: 'blob' });
  }

  getUsersExcelReportByStatus(status?: string): Observable<Blob> {
    const url = status
      ? `${this.apiUrl}/users/excel?status=${status}`
      : `${this.apiUrl}/users/excel`;
    return this.http.get(url, { responseType: 'blob' });
  }

  getUserPdfReportByBranch(branchId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/users/pdf/branch/${branchId}`, { responseType: 'blob' });
  }

  getUserExcelReportByBranch(branchId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/users/excel/branch/${branchId}`, { responseType: 'blob' });
  }
}
