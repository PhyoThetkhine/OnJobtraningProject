import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LoanVoucherComponent } from '../components/shared/loan-voucher/loan-voucher.component';
import { SMELoan, SMETerm } from '../models/sme-loan.model';
import { HpLoan, HpTerm } from '../models/hp-loan.model';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

@Injectable({
  providedIn: 'root'
})
export class VoucherService {
  constructor(private modalService: NgbModal) {}

  showVoucher(loan: SMELoan | HpLoan, terms: SMETerm[] | HpTerm[]): void {
    const modalRef = this.modalService.open(LoanVoucherComponent, {
      size: 'lg',
      backdrop: 'static',
      windowClass: 'voucher-modal'
    });

    modalRef.componentInstance.loan = loan;
    modalRef.componentInstance.terms = terms;

    // Handle close event
    modalRef.componentInstance.close.subscribe(() => {
      modalRef.close();
    });
  }

  async generatePDF(voucherElement: HTMLElement): Promise<void> {
    const canvas = await html2canvas(voucherElement, {
      useCORS: true,
      logging: false
    });

    const imgData = canvas.toDataURL('image/jpeg', 1.0);
    const pdf = new jsPDF('p', 'mm', 'a4');
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (canvas.height * pdfWidth) / canvas.width;

    pdf.addImage(imgData, 'JPEG', 0, 0, pdfWidth, pdfHeight);
    pdf.save(`loan-voucher-${Date.now()}.pdf`);
  }

  async downloadAsImage(voucherElement: HTMLElement): Promise<void> {
    const canvas = await html2canvas(voucherElement, {
      useCORS: true,
      logging: false
    });

    const link = document.createElement('a');
    link.download = `loan-voucher-${Date.now()}.jpg`;
    link.href = canvas.toDataURL('image/jpeg', 0.8);
    link.click();
  }
} 