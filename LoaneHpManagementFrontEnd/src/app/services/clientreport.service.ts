
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ClientreportService {
  private apiUrl = 'http://localhost:8080/api/cif/report';

  constructor(private http: HttpClient) {}

  generateReport(format: string, branchName?: string, status?: string) {
    let params = new HttpParams()
      .set('format', format)
      .set('page', '0') // You might want to make page/size dynamic in the future
      .set('size', '10');
    
    if (branchName) {
      params = params.set('branchName', branchName);
    }
    if (status) {
      params = params.set('status', status); // Add status to query params
    }

    console.log('Sending report request:', `${this.apiUrl}/generateCifReport`, params.toString());

    this.http.get(`${this.apiUrl}/generateCifReport`, {
      params,
      responseType: 'blob' // Expecting a file response
    }).subscribe({
      next: (blob) => {
        console.log('Report blob received:', blob);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        // Include status in filename for clarity
        const fileName = `CIF_Report_${branchName || 'All'}_${status || 'All'}.${format}`;
        a.download = fileName;
        console.log('Downloading file:', fileName);
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error generating report:', error);
      }
    });
  }
}