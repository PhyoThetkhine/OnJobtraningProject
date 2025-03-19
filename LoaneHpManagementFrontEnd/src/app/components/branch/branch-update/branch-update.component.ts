import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpClient } from '@angular/common/http';
import { BranchService } from '../../../services/branch.service';
import { ToastrService } from 'ngx-toastr';
import { Branch, BranchStatus } from '../../../models/branch.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-branch-update',
  templateUrl: './branch-update.component.html',
  styleUrls: ['./branch-update.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class BranchUpdateComponent implements OnInit {
  @Input() branch!: Branch;
  branchForm!: FormGroup;
  loading = false;
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  townshipData: any = {};

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private branchService: BranchService,
    private http: HttpClient,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadAddressData();
  }

  private initializeForm() {
    this.branchForm = this.fb.group({
      branchName: [this.branch.branchName, [Validators.required, Validators.minLength(3)]],
      address: this.fb.group({
        state: [this.branch.address.state, Validators.required],
        township: [this.branch.address.township, Validators.required],
        city: [this.branch.address.city, Validators.required],
        additionalAddress: [this.branch.address.additionalAddress]
      })
    });
  }

  private loadAddressData() {
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        this.townshipData = data;
        this.states = Object.keys(data);
        
        // Set initial townships and cities based on selected state
        const selectedState = this.branch.address.state;
        if (selectedState && this.townshipData[selectedState]) {
          this.townships = Object.keys(this.townshipData[selectedState]);
          
          const selectedTownship = this.branch.address.township;
          if (selectedTownship && this.townshipData[selectedState][selectedTownship]) {
            this.cities = this.townshipData[selectedState][selectedTownship];
          }
        }
      },
      error: (error) => {
        console.error('Error loading address data:', error);
        this.toastr.error('Failed to load address data');
      }
    });
  }

  onStateChange() {
    const selectedState = this.branchForm.get('address.state')?.value;
    if (selectedState && this.townshipData[selectedState]) {
      this.townships = Object.keys(this.townshipData[selectedState]);
      this.branchForm.patchValue({
        address: {
          township: '',
          city: ''
        }
      });
      this.cities = [];
    }
  }

  onTownshipChange() {
    const selectedState = this.branchForm.get('address.state')?.value;
    const selectedTownship = this.branchForm.get('address.township')?.value;
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.cities = cities;
      }
      this.branchForm.patchValue({
        address: {
          city: ''
        }
      });
    }
  }

  onSubmit() {
    if (this.branchForm.valid) {
      this.loading = true;

      const updateData = {
        ...this.branchForm.value,
        id: this.branch.id
      };

      this.branchService.updateBranch(updateData).subscribe({
        next: () => {
          this.activeModal.close(true);
          this.toastr.success('Branch updated successfully');
        },
        error: (error) => {
          console.error('Error updating branch:', error);
          this.toastr.error(error.error?.message || 'Failed to update branch');
          this.loading = false;
        }
      });
    } else {
      Object.keys(this.branchForm.controls).forEach(key => {
        const control = this.branchForm.get(key);
        if (control instanceof FormGroup) {
          Object.keys(control.controls).forEach(subKey => {
            control.get(subKey)?.markAsTouched();
          });
        } else {
          control?.markAsTouched();
        }
      });
    }
  }

  dismiss() {
    this.activeModal.dismiss();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.branchForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.branchForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
      if (control.errors['minlength']) {
        return `Minimum ${control.errors['minlength'].requiredLength} characters required`;
      }
    }
    return '';
  }
} 