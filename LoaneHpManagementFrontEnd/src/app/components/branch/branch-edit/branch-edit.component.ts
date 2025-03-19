import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Branch } from '../../../models/branch.model';
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
    private http: HttpClient
  ) {
    this.editForm = this.fb.group({
      branchName: ['', Validators.required],
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