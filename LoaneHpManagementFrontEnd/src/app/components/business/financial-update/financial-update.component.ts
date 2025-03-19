import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { FinancialService } from '../../../services/financial.service';
import { Financial } from '../../../models/financial.model';

@Component({
  selector: 'app-financial-update',
  templateUrl: './financial-update.component.html',
  styleUrls: ['./financial-update.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class FinancialUpdateComponent implements OnInit {
  @Input() financial!: Financial;
  @Input() companyId!: number;
  financialForm!: FormGroup;
  loading = false;

  constructor(
    private formBuilder: FormBuilder,
    private financialService: FinancialService,
    private activeModal: NgbActiveModal,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.financialForm = this.formBuilder.group({
      averageIncome: [this.financial.averageIncome, [Validators.required, Validators.min(0)]],
      expectedIncome: [this.financial.expectedIncome, [Validators.required, Validators.min(0)]],
      averageExpenses: [this.financial.averageExpenses, [Validators.required, Validators.min(0)]],
      averageInvestment: [this.financial.averageInvestment, [Validators.required, Validators.min(0)]],
      averageEmployees: [this.financial.averageEmployees, [Validators.required, Validators.min(0)]],
      averageSalaryPaid: [this.financial.averageSalaryPaid, [Validators.required, Validators.min(0)]],
      revenueProof: [this.financial.revenueProof, [Validators.required]]
    });
  }

  isFieldInvalid(field: string): boolean {
    const formControl = this.financialForm.get(field);
    return formControl ? (formControl.invalid && (formControl.dirty || formControl.touched)) : false;
  }

  getErrorMessage(field: string): string {
    const control = this.financialForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) {
        return `${field.charAt(0).toUpperCase()}${field.slice(1)} is required`;
      }
      if (control.errors['min']) {
        return `${field.charAt(0).toUpperCase()}${field.slice(1)} must be greater than or equal to 0`;
      }
    }
    return '';
  }

  onSubmit(): void {
    if (this.financialForm.valid) {
      this.loading = true;
      const updatedData = {
        ...this.financialForm.value,
        id: this.financial.id,
        companyId: this.companyId
      };

      this.financialService.updateFinancial(this.financial.id, updatedData)
        .subscribe({
          next: (response) => {
            this.toastr.success('Financial information updated successfully');
            this.activeModal.close(response);
            this.loading = false;
          },
          error: (error) => {
            console.error('Error updating financial information:', error);
            this.toastr.error('Failed to update financial information');
            this.loading = false;
          }
        });
    } else {
      Object.keys(this.financialForm.controls).forEach(key => {
        const control = this.financialForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }

  onCancel(): void {
    this.activeModal.dismiss();
  }
} 