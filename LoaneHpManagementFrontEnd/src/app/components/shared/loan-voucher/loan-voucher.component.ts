import { Component, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SMELoan, SMETerm, FREQUENCY } from '../../../models/sme-loan.model';
import { HpLoan, HpTerm } from '../../../models/hp-loan.model';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

@Component({
  selector: 'app-loan-voucher',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="voucher-modal">
      <!-- Action buttons -->
      <div class="action-buttons mb-3 d-flex justify-content-end">
        <button class="btn btn-primary me-2" (click)="downloadPDF()">
          <i class="bi bi-download me-1"></i> Download PDF
        </button>
        <button class="btn btn-secondary" (click)="closeVoucher()">
          <i class="bi bi-x-lg"></i> Close
        </button>
      </div>

      <div class="voucher-container p-4" #voucherContent>
        <!-- Header -->
        <div class="text-center mb-4">
          <h2 class="mb-1">Loan Confirmation Voucher</h2>
          <p class="text-muted mb-0">Loan Code: {{ getLoanCode() }}</p>
        </div>

        <!-- Loan Information -->
        <div class="loan-info mb-4">
          <div class="row">
            <div class="col-md-6">
              <table class="table table-borderless">
                <tbody>
                  <tr>
                    <td class="fw-bold">Customer Name:</td>
                    <td>{{ loan.cif.name }}</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Customer Code:</td>
                    <td>{{ loan.cif.cifCode }}</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Loan Amount:</td>
                    <td>{{ loan.loanAmount | number }} MMK</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Disbursement Amount:</td>
                    <td>{{ loan.disbursementAmount | number }} MMK</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="col-md-6">
              <table class="table table-borderless">
                <tbody>
                  <tr>
                    <td class="fw-bold">Duration:</td>
                    <td>{{ loan.duration }} {{ getDurationUnit() }}</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Interest Rate:</td>
                    <td>{{ loan.interestRate }}%</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Status:</td>
                    <td>{{ loan.status | titlecase }}</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Confirmation Date:</td>
                    <td>{{ loan.confirmDate | date:'mediumDate' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Repayment Schedule -->
        <div class="repayment-schedule">
          <h4 class="mb-3">Repayment Schedule</h4>
          <div class="table-responsive">
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th class="text-center">No.</th>
                  <th class="text-end">Principal</th>
                  <th class="text-end">Interest</th>
                  <th class="text-center">Due Date</th>
                  <th class="text-center">Status</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let term of terms; let i = index">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td class="text-end">{{ term.principal | number }}</td>
                  <td class="text-end">{{ term.interest | number }}</td>
                  <td class="text-center">{{ term.dueDate | date:'mediumDate' }}</td>
                  <td class="text-center">
                    <span [class]="'badge ' + getStatusBadgeClass(term.status)">
                      {{ term.status | titlecase }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Footer -->
        <div class="mt-4 pt-4 border-top">
          <div class="row">
            <div class="col-6 text-center">
              <p class="mb-5">_________________</p>
              <p class="mb-0">Customer's Signature</p>
            </div>
            <div class="col-6 text-center">
              <p class="mb-5">_________________</p>
              <p class="mb-0">Authorized Signature</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .voucher-modal {
      background: rgba(0, 0, 0, 0.1);
      padding: 20px;
      border-radius: 12px;
    }
    .voucher-container {
      background: white;
      border: 1px solid #ddd;
      border-radius: 8px;
      max-width: 1000px;
      margin: auto;
      min-height: 100%;
    }
    .loan-info {
      background: #f8f9fa;
      border-radius: 4px;
      padding: 1rem;
    }
    .table-borderless td {
      padding: 0.5rem;
    }
    .repayment-schedule {
      margin-bottom: 2rem;
      background: white;
    }
    @media print {
      .action-buttons {
        display: none !important;
      }
      .voucher-container {
        border: none;
        background: white !important;
      }
      .repayment-schedule {
        page-break-inside: avoid;
        background: white !important;
      }
      .table {
        page-break-inside: auto;
        background: white !important;
      }
      tr {
        page-break-inside: avoid;
        page-break-after: auto;
      }
      thead {
        display: table-header-group;
      }
      tfoot {
        display: table-footer-group;
      }
      body {
        background: white !important;
      }
    }
  `]
})
export class LoanVoucherComponent {
  @Input() loan!: SMELoan | HpLoan;
  @Input() terms: (SMETerm | HpTerm)[] = [];
  @Output() close = new EventEmitter<void>();
  @ViewChild('voucherContent') voucherContent!: ElementRef;

  get isSmeLoan(): boolean {
    return 'smeLoanCode' in this.loan;
  }

  getLoanCode(): string {
    return this.isSmeLoan 
      ? (this.loan as SMELoan).smeLoanCode 
      : (this.loan as HpLoan).hpLoanCode;
  }

  getDurationUnit(): string {
    if (this.isSmeLoan) {
      const smeLoan = this.loan as SMELoan;
      return smeLoan.frequency === FREQUENCY.MONTHLY ? 'months' : 'years';
    }
    return 'months'; // HP loans are always in months
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'under_schedule':
        return 'bg-warning text-dark';
      case 'paid':
        return 'bg-success';
      case 'overdue':
        return 'bg-danger';
      case 'partially_paid':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }

  async downloadPDF() {
    try {
      const content = this.voucherContent.nativeElement;
      const canvas = await html2canvas(content, {
        useCORS: true,
        allowTaint: true
      });
      
      const imgWidth = 210; // A4 width in mm
      const pageHeight = 297; // A4 height in mm
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;

      const pdf = new jsPDF('p', 'mm', 'a4');
      const imgData = canvas.toDataURL('image/png');

      let position = 0;

      // First page
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      // Add new pages if content exceeds first page
      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      pdf.save(`loan-voucher-${this.getLoanCode()}.pdf`);
    } catch (error) {
      console.error('Error generating PDF:', error);
    }
  }

  closeVoucher() {
    this.close.emit();
  }
} 