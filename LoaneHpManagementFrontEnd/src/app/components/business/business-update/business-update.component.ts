import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CompanyService } from '../../../services/company.service';
import { Company } from '../../../models/company.model';
import { HttpClient } from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';
import { CloudinaryService } from 'src/app/services/cloudinary.service';

@Component({
  selector: 'app-business-update',
  templateUrl: './business-update.component.html',
  styleUrls: ['./business-update.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class BusinessUpdateComponent implements OnInit {
  @Input() company!: Company;
  businessForm!: FormGroup;
  loading = false;

  // Address data
  townshipData: any = {};
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  error: null | undefined;
  businessPhotos: any;
  businessId: any;
  cifId: any;

  constructor(
    private formBuilder: FormBuilder,
    private companyService: CompanyService,
    private activeModal: NgbActiveModal,
    private toastr: ToastrService,
    private http: HttpClient,
    private authService: AuthService,
     private cloudinaryService: CloudinaryService,
    
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadTownshipData();
  }

  private loadTownshipData() {
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        this.townshipData = data;
        this.states = Object.keys(this.townshipData);
        
        // Initialize townships and cities based on current values
        const currentState = this.company.address.state;
        if (currentState && this.townshipData[currentState]) {
          this.townships = Object.keys(this.townshipData[currentState]);
          
          const currentTownship = this.company.address.township;
          if (currentTownship && this.townshipData[currentState][currentTownship]) {
            this.cities = this.townshipData[currentState][currentTownship];
          }
        }
      },
      error: (error) => {
        console.error('Error loading townships:', error);
        this.toastr.error('Failed to load address data');
      }
    });
  }

  private initForm(): void {
    this.businessForm = this.formBuilder.group({
      name: [this.company.name, [Validators.required]],
      companyType: [this.company.companyType, [Validators.required]],
      category: [this.company.category, [Validators.required]],
      businessType: [this.company.businessType, [Validators.required]],
      registrationDate: [this.formatDate(this.company.registrationDate), [Validators.required]],
      licenseNumber: [this.company.licenseNumber, [Validators.required]],
      licenseIssueDate: [this.formatDate(this.company.licenseIssueDate), [Validators.required]],
      licenseExpiryDate: [this.formatDate(this.company.licenseExpiryDate), [Validators.required]],
      phoneNumber: [this.company.phoneNumber, [Validators.required]],
      address: this.formBuilder.group({
        state: [this.company.address.state, [Validators.required]],
        township: [this.company.address.township, [Validators.required]],
        city: [this.company.address.city, [Validators.required]],
        additionalAddress: [this.company.address.additionalAddress]
      })
    });

    // Setup address change listeners
    const addressForm = this.businessForm.get('address');
    if (addressForm) {
      addressForm.get('state')?.valueChanges.subscribe(state => {
        if (state !== this.company.address.state) {
          // Only reset if the state has changed from the original
          this.onStateChange(true);
        } else {
          this.onStateChange(false);
        }
      });

      addressForm.get('township')?.valueChanges.subscribe(township => {
        if (township !== this.company.address.township) {
          // Only reset if the township has changed from the original
          this.onTownshipChange(true);
        } else {
          this.onTownshipChange(false);
        }
      });
    }
  }

  private formatDate(date: Date | string): string {
    const d = new Date(date);
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
  }

  onStateChange(resetDependents: boolean = true): void {
    const selectedState = this.businessForm.get('address.state')?.value;
    if (selectedState && this.townshipData[selectedState]) {
      this.townships = Object.keys(this.townshipData[selectedState]);
      
      if (resetDependents) {
        this.businessForm.patchValue({
          address: {
            township: '',
            city: ''
          }
        });
        this.cities = [];
      }
    }
  }

  onTownshipChange(resetCity: boolean = true): void {
    const selectedState = this.businessForm.get('address.state')?.value;
    const selectedTownship = this.businessForm.get('address.township')?.value;
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.cities = cities;
        
        if (resetCity) {
          this.businessForm.patchValue({
            address: {
              city: ''
            }
          });
        }
      }
    }
  }

  isFieldInvalid(field: string): boolean {
    const formControl = this.businessForm.get(field);
    return formControl ? (formControl.invalid && (formControl.dirty || formControl.touched)) : false;
  }

  getErrorMessage(field: string): string {
    const control = this.businessForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) {
        return `${field.split('.').pop()?.charAt(0).toUpperCase()}${field.split('.').pop()?.slice(1)} is required`;
      }
    }
    return '';
  }

  async onSubmit() {
    if (this.businessForm.valid) {
      this.loading = true;
      this.error = null;

      try {
        // Get authentication token - same as create method
        const token = this.authService.getToken();
        if (!token) {
          throw new Error('No authentication token found. Please log in.');
        }

        // Upload business photos if there are new ones
        const businessPhotoUrls: string[] = [];
        if (this.businessPhotos && this.businessPhotos.length > 0) {
          for (const photo of this.businessPhotos) {
            const uploadResult = await this.cloudinaryService.uploadImage(photo.file).toPromise();
            if (uploadResult) {
              businessPhotoUrls.push(uploadResult.secure_url);
            }
          }
        }

        // Get current user - same as create method
        const currentUser = await this.authService.getCurrentUser().toPromise();
        if (!currentUser) {
          throw new Error('No authenticated user found');
        }

        // Prepare business data for update
        const businessData = {
          id: this.businessId, // Add the business ID for update (assuming you have this)
          name: this.businessForm.value.name,
          companyType: this.businessForm.value.companyType,
          businessType: this.businessForm.value.businessType,
          category: this.businessForm.value.category,
          registrationDate: new Date(this.businessForm.value.registrationDate).toISOString(),
          licenseNumber: this.businessForm.value.licenseNumber,
          licenseIssueDate: new Date(this.businessForm.value.licenseIssueDate).toISOString().split('T')[0],
          licenseExpiryDate: new Date(this.businessForm.value.licenseExpiryDate).toISOString().split('T')[0],
          phoneNumber: this.businessForm.value.phoneNumber,
          updatedUserId: currentUser.id, // Using updatedUserId instead of createdUserId
          cifId: this.cifId,
          state: this.businessForm.value.address.state,
          city: this.businessForm.value.address.city,
          township: this.businessForm.value.address.township,
          address: this.businessForm.value.address.additionalAddress || '',
          ...(businessPhotoUrls.length > 0 && { businessPhotos: businessPhotoUrls }) // Only add if new photos exist
        };

        console.log('Updating business:', JSON.stringify(businessData, null, 2));

        // Update company instead of create
        const updateCompany = await this.companyService.updateCompany(this.businessId, businessData).toPromise();
        this.toastr.success('Business updated successfully');
        this.activeModal.close(true);
      } catch (error: any) {
        console.error('Error updating business:', error);
        this.error = error.message || 'Failed to update business';
        this.toastr.error(this.error ?? 'An unknown error occurred');
      } finally {
        this.loading = false;
      }
    } else {
      // Same validation handling as create method
      this.businessForm.markAllAsTouched();
      this.toastr.error('Please fill all required fields');
    }
  }

  onCancel(): void {
    this.activeModal.dismiss();
  }
}
