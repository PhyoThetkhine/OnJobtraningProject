import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CompanyService } from '../../../services/company.service';
import { Company } from '../../../models/company.model';
import { HttpClient } from '@angular/common/http';

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

  constructor(
    private formBuilder: FormBuilder,
    private companyService: CompanyService,
    private activeModal: NgbActiveModal,
    private toastr: ToastrService,
    private http: HttpClient
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

  onSubmit(): void {
    if (this.businessForm.valid) {
      this.loading = true;
      const updatedData = {
        ...this.businessForm.value,
        id: this.company.id
      };

      this.companyService.updateCompany(this.company.id, updatedData)
        .subscribe({
          next: (response) => {
            this.toastr.success('Business information updated successfully');
            this.activeModal.close(response);
            this.loading = false;
          },
          error: (error) => {
            console.error('Error updating business:', error);
            this.toastr.error('Failed to update business information');
            this.loading = false;
          }
        });
    } else {
      Object.keys(this.businessForm.controls).forEach(key => {
        const control = this.businessForm.get(key);
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
