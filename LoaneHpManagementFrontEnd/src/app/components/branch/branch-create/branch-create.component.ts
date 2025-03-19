import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { BranchService } from '../../../services/branch.service';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { CreateBranchDto, BranchStatus } from '../../../models/branch.model';

@Component({
  selector: 'app-branch-create',
  templateUrl: './branch-create.component.html',
  styleUrls: ['./branch-create.component.css'],
  standalone:false
})
export class BranchCreateComponent implements OnInit {
  @Input() isModal = false;
  @Input() isEdit = false;

  branchForm!: FormGroup;
  loading = false;
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  townshipData: any;

  constructor(
    private fb: FormBuilder,
    private branchService: BranchService,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private toastr: ToastrService
  ) {
    this.initializeForm();
  }

  ngOnInit() {
    this.loadAddressData();
  }

  private initializeForm() {
    this.branchForm = this.fb.group({
      branchName: ['', [Validators.required, Validators.minLength(3)]],
      status: [BranchStatus.ACTIVE],
      address: this.fb.group({
        state: ['', Validators.required],
        township: ['', Validators.required],
        city: ['', Validators.required],
        additionalAddress: ['']
      })
    });
  }

  private loadAddressData() {
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        this.townshipData = data;
        this.states = Object.keys(data);
      },
      error: (error) => {
        console.error('Error loading address data:', error);
        this.toastr.error('Failed to load address data');
      }
    });
  }

  onStateChange() {
    const state = this.branchForm.get('address.state')?.value;
    if (state && this.townshipData[state]) {
      this.townships = Object.keys(this.townshipData[state]);
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
    const state = this.branchForm.get('address.state')?.value;
    const township = this.branchForm.get('address.township')?.value;
    if (state && township && this.townshipData[state][township]) {
      this.cities = this.townshipData[state][township];
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

      this.authService.getCurrentUser().subscribe({
        next: (currentUser) => {
          const branchData: CreateBranchDto = {
            branchName: this.branchForm.value.branchName,
            address: this.branchForm.value.address,
            createdUser: {
              id: currentUser.id
            },
  
          };

          this.branchService.createBranch(branchData).subscribe({
            next: (response) => {
              this.toastr.success('Branch created successfully');
              if (this.isModal) {
                this.closeModal();
              } else {
                this.router.navigate(['/branches']);
              }
            },
            error: (error) => {
              console.error('Error creating branch:', error);
              this.toastr.error(error.error?.message || 'Failed to create branch');
              this.loading = false;
            }
          });
        },
        error: (error) => {
          console.error('Error getting current user:', error);
          this.toastr.error('Failed to get current user');
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.branchForm);
    }
  }

  closeModal() {
    const modal = document.getElementById('addBranchModal');
    if (modal) {
      const bootstrapModal = (window as any).bootstrap.Modal.getInstance(modal);
      if (bootstrapModal) {
        bootstrapModal.hide();
      }
    }
  }

  cancel() {
    if (this.isModal) {
      this.closeModal();
    } else {
      this.router.navigate(['/branches']);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  getStatusText(status: BranchStatus): string {
    switch (status) {
      case BranchStatus.ACTIVE:
        return 'Active';
      case BranchStatus.TERMINATED:
        return 'Terminated';
      case BranchStatus.CLOSED:
        return 'Closed';
      default:
        return 'Unknown';
    }
  }

  getStatusBadgeClass(status: BranchStatus): string {
    switch (status) {
      case BranchStatus.ACTIVE:
        return 'badge bg-success';
      case BranchStatus.TERMINATED:
        return 'badge bg-danger';
      case BranchStatus.CLOSED:
        return 'badge bg-secondary';
      default:
        return 'badge bg-secondary';
    }
  }
}
