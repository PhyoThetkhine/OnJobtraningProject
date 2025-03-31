import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BranchreportService {

  private apiUrl = 'http://localhost:8080/api/branch/report/generate';

  constructor(private http : HttpClient) { }

    // Method to download the report in the requested format (pdf or xls)
    generateReport(format: string): void {
      const headers = new HttpHeaders().set('Accept', 'application/octet-stream'); // Expect a binary stream
      const params = new HttpParams().set('format', format); // Pass format as query parameter

      this.http
        .get(this.apiUrl, {
          headers,
          responseType: 'blob', // Set response type to 'blob' to handle binary data
          params: { format } // Pass the format parameter to the backend
        })
        .subscribe((response: Blob) => {
          const fileName = `branch_report.${format}`; // Dynamically set file name based on format
          const link = document.createElement('a');
          link.href = URL.createObjectURL(response); // Create an object URL for the response
          link.download = fileName; // Set the download attribute to trigger download
          link.click();
        }, (error) => {
          console.error('Error downloading the report:', error);
        });
    }
}
function saveAs(response: Blob, fileName: string) {
  throw new Error('Function not implemented.');
}

