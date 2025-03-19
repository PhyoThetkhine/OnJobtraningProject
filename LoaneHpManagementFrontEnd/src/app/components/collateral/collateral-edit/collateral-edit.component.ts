import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CollateralService } from '../../../services/collateral.service';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { AuthService } from '../../../services/auth.service';
import { Collateral } from '../../../models/collateral.model';

@Component({
  selector: 'app-collateral-edit',
  templateUrl: './collateral-edit.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CollateralEditComponent implements OnInit {
  @Input() collateral!: Collateral;
  collateralForm!: FormGroup;
  loading = false;
  documentPreview: string | null = null;
  private documentFile: File | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private collateralService: CollateralService,
    private cloudinaryService: CloudinaryService,
    private authService: AuthService,
    private activeModal: NgbActiveModal,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.documentPreview = this.collateral.documentUrl;
  }

  private initForm(): void {
    this.collateralForm = this.formBuilder.group({
      propertyType: [this.collateral.propertyType, Validators.required],
      estimatedValue: [this.collateral.estimatedValue, [Validators.required, Validators.min(0)]],
      document: ['']
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.documentFile = file;
      this.documentPreview = URL.createObjectURL(file);
      this.collateralForm.patchValue({ document: file });
    }
  }

  isFieldInvalid(field: string): boolean {
    const formControl = this.collateralForm.get(field);
    return formControl ? (formControl.invalid && (formControl.dirty || formControl.touched)) : false;
  }

  getErrorMessage(field: string): string {
    const control = this.collateralForm.get(field);
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

  async onSubmit() {
    if (this.collateralForm.valid) {
      this.loading = true;

      try {
        let documentUrl = this.collateral.documentUrl;

        // Upload new document if selected
        if (this.documentFile) {
          const uploadResult = await this.cloudinaryService.uploadImage(this.documentFile).toPromise();
          if (!uploadResult) {
            throw new Error('Failed to upload document');
          }
          documentUrl = uploadResult.secure_url;
        }

        // Get current user
        const currentUser = await this.authService.getCurrentUser().toPromise();
        if (!currentUser) {
          throw new Error('No authenticated user found');
        }

        const collateralData = {
          ...this.collateralForm.value,
          documentUrl,
          updatedUserId: currentUser.id
        };

        // Update collateral
        await this.collateralService.updateCollateral(this.collateral.id, collateralData).toPromise();
        this.toastr.success('Collateral updated successfully');
        this.activeModal.close(true);
      } catch (error) {
        console.error('Error updating collateral:', error);
        this.toastr.error('Failed to update collateral');
      } finally {
        this.loading = false;
      }
    } else {
      Object.keys(this.collateralForm.controls).forEach(key => {
        const control = this.collateralForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }

  onCancel(): void {
    if (this.documentPreview && this.documentPreview !== this.collateral.documentUrl) {
      URL.revokeObjectURL(this.documentPreview);
    }
    this.activeModal.dismiss();
  }

  ngOnDestroy(): void {
    if (this.documentPreview && this.documentPreview !== this.collateral.documentUrl) {
      URL.revokeObjectURL(this.documentPreview);
    }
  }
} 