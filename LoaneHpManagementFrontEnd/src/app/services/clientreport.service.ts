import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ClientreportService {
  private apiUrl = 'http://localhost:8080/api/cif/report';

  constructor(private http: HttpClient) {}

  generateReport(format: string, branchName?: string) {
    let params = new HttpParams()
      .set('format', format)
      .set('page', '0')
      .set('size', '10');
    if (branchName) {
      params = params.set('branchName', branchName);
    }
    console.log('Sending report request:', `${this.apiUrl}/generateCifReport`, params.toString());

    this.http.get(`${this.apiUrl}/generateCifReport`, {
      params,
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        console.log('Report blob received:', blob);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `CIF_Report_${branchName || 'All'}.${format}`;
        console.log('Downloading file:', a.download);
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error generating report:', error);
      }
    });
  }
}