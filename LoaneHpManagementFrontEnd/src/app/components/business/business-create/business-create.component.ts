import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { CompanyService } from '../../../services/company.service';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-business-create',
  templateUrl: './business-create.component.html',
  styleUrls: ['./business-create.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class BusinessCreateComponent implements OnInit {
  @Input() cifId!: number;
  businessForm!: FormGroup;
  loading = false;
  error: string | null = null;
  townshipData: any = {};
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  businessPhotos: { file: File, preview: string }[] = [];

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private http: HttpClient,
    private cloudinaryService: CloudinaryService,
    private companyService: CompanyService,
    private toastr: ToastrService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadTownshipData();
  }

  private initializeForm() {
    this.businessForm = this.fb.group({
      name: ['', Validators.required],
      companyType: ['', Validators.required],
      businessType: ['', Validators.required],
      category: ['', Validators.required],
      registrationDate: ['', Validators.required],
      licenseNumber: ['', Validators.required],
      licenseIssueDate: ['', Validators.required],
      licenseExpiryDate: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      address: this.fb.group({
        state: ['', Validators.required],
        township: ['', Validators.required],
        city: ['', Validators.required],
        additionalAddress: ['']
      }),
      financial: this.fb.group({
        averageIncome: ['', [Validators.required, Validators.min(0)]],
        expectedIncome: ['', [Validators.required, Validators.min(0)]],
        averageExpenses: ['', [Validators.required, Validators.min(0)]],
        averageInvestment: ['', [Validators.required, Validators.min(0)]],
        averageEmployees: ['', [Validators.required, Validators.min(0)]],
        averageSalaryPaid: ['', [Validators.required, Validators.min(0)]],
        revenueProof: ['', Validators.required]
      }),
      businessPhotos: [[], [Validators.required, Validators.minLength(2)]]
    });
  }

  private loadTownshipData() {
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        this.townshipData = data;
        this.states = Object.keys(this.townshipData);
      },
      error: (error) => {
        console.error('Error loading townships:', error);
        this.toastr.error('Failed to load address data');
      }
    });
  }

  onStateChange() {
    const selectedState = this.businessForm.get('address.state')?.value;
    if (selectedState && this.townshipData[selectedState]) {
      this.townships = Object.keys(this.townshipData[selectedState]);
      this.businessForm.patchValue({
        address: {
          township: '',
          city: ''
        }
      });
      this.cities = [];
    }
  }

  onTownshipChange() {
    const selectedState = this.businessForm.get('address.state')?.value;
    const selectedTownship = this.businessForm.get('address.township')?.value;
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.cities = cities;
      }
      this.businessForm.patchValue({
        address: {
          city: ''
        }
      });
    }
  }

  async onBusinessPhotoSelected(event: any) {
    const file = event.target.files[0];
    if (file && this.businessPhotos.length < 2) {
      const previewUrl = URL.createObjectURL(file);
      this.businessPhotos.push({ file, preview: previewUrl });
      this.businessForm.patchValue({ 
        businessPhotos: this.businessPhotos.map(photo => photo.file)
      });
    }
  }

  removeBusinessPhoto(index: number) {
    URL.revokeObjectURL(this.businessPhotos[index].preview);
    this.businessPhotos.splice(index, 1);
    this.businessForm.patchValue({ 
      businessPhotos: this.businessPhotos.map(photo => photo.file) 
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.businessForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.businessForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
      if (control.errors['min']) {
        return 'Value must be greater than or equal to 0';
      }
      if (control.errors['minlength']) {
        return `Minimum ${control.errors['minlength'].requiredLength} items required`;
      }
    }
    return '';
  }
  async onSubmit() {
    if (this.businessForm.valid) {
      this.loading = true;
      this.error = null;
  
      try {
        const token = this.authService.getToken();
        if (!token) {
          throw new Error('No authentication token found. Please log in.');
        }
  
        // Upload business photos
        const businessPhotoUrls: string[] = [];
        for (const photo of this.businessPhotos) {
          const uploadResult = await this.cloudinaryService.uploadImage(photo.file).toPromise();
          if (uploadResult) {
            businessPhotoUrls.push(uploadResult.secure_url);
          }
        }
  
        // Get current user
        const currentUser = await this.authService.getCurrentUser().toPromise();
        if (!currentUser) {
          throw new Error('No authenticated user found');
        }
  
        // Prepare business data including financials and photo URLs
        const businessData = {
          name: this.businessForm.value.name,
          companyType: this.businessForm.value.companyType,
          businessType: this.businessForm.value.businessType,
          category: this.businessForm.value.category,
          registrationDate: new Date(this.businessForm.value.registrationDate).toISOString(),// Already 'YYYY-MM-DD'
          licenseNumber: this.businessForm.value.licenseNumber,
          licenseIssueDate: this.businessForm.value.licenseIssueDate, // 'YYYY-MM-DD'
          licenseExpiryDate: this.businessForm.value.licenseExpiryDate, // 'YYYY-MM-DD'
          phoneNumber: this.businessForm.value.phoneNumber,
          createdUserId: currentUser.id,
          cifId: this.cifId,
          state: this.businessForm.value.address.state,
          city: this.businessForm.value.address.city,
          township: this.businessForm.value.address.township,
          address: this.businessForm.value.address.additionalAddress || '',
          financial: {
            averageIncome: this.businessForm.value.financial.averageIncome,
            expectedIncome: this.businessForm.value.financial.expectedIncome,
            averageExpenses: this.businessForm.value.financial.averageExpenses,
            averageInvestment: this.businessForm.value.financial.averageInvestment,
            averageEmployees: this.businessForm.value.financial.averageEmployees,
            averageSalaryPaid: this.businessForm.value.financial.averageSalaryPaid,
            revenueProof: this.businessForm.value.financial.revenueProof
          },
          businessPhotoUrls: businessPhotoUrls // Include photo URLs
        };
  
        console.log('Submitting business:', JSON.stringify(businessData, null, 2));
  
        const createCompany = await this.companyService.createCompany(businessData).toPromise();
        this.toastr.success('Business created successfully');
        this.activeModal.close(true);
      } catch (error: any) {
        console.error('Error creating business:', error);
        this.error = error.message || 'Failed to create business';
        this.toastr.error(this.error ?? 'An unknown error occurred');
      } finally {
        this.loading = false;
      }
    } else {
      this.businessForm.markAllAsTouched();
      this.toastr.error('Please fill all required fields');
    }
  }

  dismiss() {
    this.activeModal.dismiss();
  }
}