import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CollateralService } from '../../../services/collateral.service';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-collateral-create',
  templateUrl: './collateral-create.component.html',
  styleUrls: ['./collateral-create.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CollateralCreateComponent implements OnInit {
  @Input() cifId!: number;
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
  }

  private initForm(): void {
    this.collateralForm = this.formBuilder.group({
      propertyType: ['', Validators.required],
      estimatedValue: ['', [Validators.required, Validators.min(0)]],
     
      document: ['', Validators.required]
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
    if (this.collateralForm.valid && this.documentFile) {
      this.loading = true;

      try {
        // Upload document to Cloudinary
        const uploadResult = await this.cloudinaryService.uploadImage(this.documentFile).toPromise();
        if (!uploadResult) {
          throw new Error('Failed to upload document');
        }

        // Get current user
        const currentUser = await this.authService.getCurrentUser().toPromise();
        if (!currentUser) {
          throw new Error('No authenticated user found');
        }

        const collateralData = {
          ...this.collateralForm.value,
          documentUrl: uploadResult.secure_url,
          cifId: this.cifId,
          createdUserId: currentUser.id
        };
        

        // Create collateral
        await this.collateralService.createCollateral(collateralData).toPromise();
        console.log("collateralData"+collateralData)
        this.toastr.success('Collateral added successfully');
        this.activeModal.close(true);
      } catch (error) {
        console.error('Error creating collateral:', error);
        this.toastr.error('Failed to create collateral');
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
    if (this.documentPreview) {
      URL.revokeObjectURL(this.documentPreview);
    }
    this.activeModal.dismiss();
  }

  ngOnDestroy(): void {
    if (this.documentPreview) {
      URL.revokeObjectURL(this.documentPreview);
    }
  }
} 