import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Branch } from '../../../models/branch.model';
import { catchError, map, Observable, of, switchMap, timer } from 'rxjs';
import { BranchService } from 'src/app/services/branch.service';
declare var bootstrap: any;

@Component({
  selector: 'app-branch-edit',
  templateUrl: './branch-edit.component.html',
  styleUrls: ['./branch-edit.component.css'],
  standalone: false
})
export class BranchEditComponent implements OnInit {
  @Input() branch: Branch | null = null;
  @Output() submitSuccess = new EventEmitter<Branch>();

  editForm: FormGroup;
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  townshipData: any;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private branchService: BranchService,
  ) {
    this.editForm = this.fb.group({
      branchName: ['', 
        [Validators.required, Validators.minLength(3)],
        [this.duplicateBranchValidator()]
      ],
      address: this.fb.group({
        state: ['', Validators.required],
        township: ['', Validators.required],
        city: ['', Validators.required],
        additionalAddress: ['']
      })
    });
  }

  ngOnInit() {
    this.loadTownshipData().then(() => {
      if (this.branch) {
        this.populateForm();
      }
    });
  }

  private duplicateBranchValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value || control.value.length < 3) {
        return of(null);
      }
      return timer(500).pipe(
        switchMap(() => this.branchService.getAllBranches()),
        map((branches: Branch[]) => {
          const inputName = this.normalizeBranchName(control.value);
          const isDuplicate = branches.some(branch => 
            this.normalizeBranchName(branch.branchName) === inputName &&
            (!this.branch || branch.id !== this.branch.id) // Exclude current branch
          );
          return isDuplicate ? { duplicateBranch: true } : null;
        }),
        catchError(() => of(null))
      );
    };
  }

  private normalizeBranchName(name: string): string {
    return name.replace(/\s+/g, '').toLowerCase();
  }

  private async loadTownshipData() {
    try {
      this.townshipData = await this.http.get('assets/myanmar-townships.json').toPromise();
      this.states = Object.keys(this.townshipData);
    } catch (error) {
      console.error('Error loading township data:', error);
    }
  }

  private populateForm() {
    if (!this.branch) return;

    this.editForm.patchValue({
      branchName: this.branch.branchName,
      address: {
        state: this.branch.address.state,
        township: this.branch.address.township,
        city: this.branch.address.city,
        additionalAddress: this.branch.address.additionalAddress
      }
    });

    if (this.branch.address.state) {
      this.onStateChange();
      if (this.branch.address.township) {
        this.onTownshipChange();
      }
    }
  }

  onStateChange() {
    const state = this.editForm.get('address.state')?.value;
    if (state) {
      this.townships = Object.keys(this.townshipData[state]);
      this.cities = [];
    }
  }

  onTownshipChange() {
    const state = this.editForm.get('address.state')?.value;
    const township = this.editForm.get('address.township')?.value;
    if (state && township) {
      this.cities = this.townshipData[state][township];
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.editForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched) && !field.pending) : false;
  }

  onSubmit() {
    if (this.editForm.valid && this.branch) {
      const formData = this.editForm.value;
      const updatedBranch: Branch = {
        ...this.branch,
        ...formData,
        updatedDate: new Date()
      };

      console.log('Updating branch:', updatedBranch);
      this.submitSuccess.emit(updatedBranch);
      // Modal will be closed by the parent component after handling the update
    }
  }

  closeModal() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('editBranchModal'));
    if (modal) {
      modal.hide();
    }
  }
}