import { Component } from '@angular/core';
import { Observable, throwError } from 'rxjs';

import { map, switchMap } from 'rxjs/operators';
import { AUTHORITY } from 'src/app/models/role.model';
import { User } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';
import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';


@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css'],
  standalone:false
})
export class ReportsComponent {
  loadingReport = false;
  reportError: string | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  generateReport(format: 'pdf' | 'excel') {
    this.loadingReport = true;
    this.reportError = null;

    this.authService.getCurrentUser().pipe(
      switchMap(currentUser => {
        if (currentUser.role.authority === AUTHORITY.MainBranchLevel) {
          return this.userService.getUsers(0, 1000).pipe(
            map(response => response.content)
          );
        } else {
          return this.userService.getUsersByBranch(currentUser.branch.id, 0, 1000).pipe(
            map(response => response.content)
          );
        }
      }),
      switchMap(users => {
        if (format === 'pdf') {
          return this.generatePdf(users);
        }
        // Add Excel handling here if needed
        return throwError(() => new Error('Format not supported'));
      })
    ).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `users_report_${new Date().toISOString()}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.loadingReport = false;
      },
      error: (error) => {
        console.error('Error generating report:', error);
        this.reportError = error.message || 'Failed to generate report. Please try again.';
        this.loadingReport = false;
      },
      complete: () => this.loadingReport = false
    });
  }

  private generatePdf(users: User[]): Observable<Blob> {
    return new Observable(observer => {
      const docDefinition = {
        content: [
          { text: 'User Report', style: 'header' },
          this.buildTable(users)
        ],
        styles: {
          header: {
            fontSize: 24,
            bold: true,
            margin: [0, 0, 0, 20] as [number, number, number, number] // Explicit tuple type
          },
          tableHeader: {
            bold: true,
            fontSize: 14,
            color: '#444'
          }
        }
      };

      const pdfDoc = pdfMake.createPdf(docDefinition);
      pdfDoc.getBlob((blob: Blob) => {
        observer.next(blob);
        observer.complete();
      });
    });
  }

  private buildTable(users: User[]): any {
    return {
      table: {
        headerRows: 1,
        widths: ['auto', '*', '*', '*', 'auto'],
        body: [
          [
            { text: 'ID', style: 'tableHeader' },
            { text: 'Name', style: 'tableHeader' },
            { text: 'Email', style: 'tableHeader' },
            { text: 'Phone', style: 'tableHeader' },
           
            { text: 'Status', style: 'tableHeader' },
          ],
          ...users.map(user => [
            user.id.toString(),
            // `${user.firstName} ${user.lastName}`,
            user.email,
            user.phoneNumber,
            user.status
          ])
        ]
      }
    };
  }
}