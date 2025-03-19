import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors, AsyncValidatorFn } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CIFService } from '../../../services/cif.service';
import { ToastrService } from 'ngx-toastr';
import { CIF, CIFType } from '../../../models/cif.model';
import { HttpClient } from '@angular/common/http';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { CommonModule } from '@angular/common';
import { Observable, Subject } from 'rxjs';
import { map, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { NrcService, NrcTownship } from '../../../services/nrc.service';

@Component({
  selector: 'app-client-update',
  templateUrl: './client-update.component.html',
  styleUrls: ['./client-update.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class ClientUpdateComponent implements OnInit, OnDestroy {
  @Input() client!: CIF;
  personalForm!: FormGroup;
  addressForm!: FormGroup;
  nrcForm!: FormGroup;
  loading = false;
  error: string | null = null;
  townshipData: any = {};
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  CIFType = CIFType;
  
  // NRC related properties
  nrcStateNumbers: string[] = [];
  nrcTownships: NrcTownship[] = [];
  nrcTypes: string[] = [];
  filteredNrcTownships: NrcTownship[] = [];
  
  // Photo previews
  photoPreview: string | null = null;
  nrcFrontPhotoPreview: string | null = null;
  nrcBackPhotoPreview: string | null = null;
  private profilePhoto: File | null = null;
  private nrcFrontFile: File | null = null;
  private nrcBackFile: File | null = null;

  private destroy$ = new Subject<void>();
  private existingEmails: Set<string> = new Set();
  private existingPhoneNumbers: Set<string> = new Set();

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private cifService: CIFService,
    private toastr: ToastrService,
    private http: HttpClient,
    private cloudinaryService: CloudinaryService,
    private nrcService: NrcService
  ) {}

  ngOnInit() {
    this.initializeForms();
    this.loadExistingData();
    this.loadTownshipData();
    this.loadNrcData();
  }

  private parseNrcString(nrcString: string): { stateNumber: string, townshipCode: string, type: string, number: string } {
    try {
      const parts = nrcString.split('/');
      if (parts.length !== 2) throw new Error('Invalid NRC format');

      const stateNumber = parts[0];
      const matches = parts[1].match(/([A-Za-z]+)\(([A-Za-z]+)\)(\d+)/);
      if (!matches) throw new Error('Invalid NRC format');

      return {
        stateNumber: stateNumber,
        townshipCode: matches[1],
        type: matches[2],
        number: matches[3]
      };
    } catch (error) {
      console.error('Error parsing NRC:', error);
      return {
        stateNumber: '',
        townshipCode: '',
        type: '',
        number: ''
      };
    }
  }

  private initializeForms() {
    // Initialize Personal Information Form
    this.personalForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', {
        validators: [Validators.required, Validators.email],
        asyncValidators: [this.duplicateEmailValidator()],
        updateOn: 'blur'
      }],
      phoneNumber: ['', {
        validators: [Validators.required],
        asyncValidators: [this.duplicatePhoneValidator()],
        updateOn: 'blur'
      }],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      cifType: ['', Validators.required],
      photo: [null]
    });

    // Initialize Address Form
    this.addressForm = this.fb.group({
      state: ['', Validators.required],
      township: ['', Validators.required],
      city: ['', Validators.required],
      additionalAddress: ['']
    });

    // Initialize NRC Form
    this.nrcForm = this.fb.group({
      stateNumber: ['', Validators.required],
      townshipCode: ['', Validators.required],
      type: ['', Validators.required],
      number: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(6),
        Validators.pattern('^[0-9]*$')
      ]],
      nrcFrontPhoto: [''],
      nrcBackPhoto: ['']
    });

    // Set up form value change subscriptions
    this.setupFormSubscriptions();

    // Patch forms with client data if available
    if (this.client) {
      this.patchFormsWithClientData();
    }
  }

  private setupFormSubscriptions() {
    // Email validation subscription
    this.personalForm.get('email')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.personalForm.get('email')?.updateValueAndValidity();
    });

    // Phone number validation subscription
    this.personalForm.get('phoneNumber')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.personalForm.get('phoneNumber')?.updateValueAndValidity();
    });

    // NRC state number change subscription
    this.nrcForm.get('stateNumber')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.filterNrcTownships();
        this.nrcForm.patchValue({ townshipCode: '' });
      });
  }

  private patchFormsWithClientData() {
    if (!this.client) return;

    // Patch Personal Form
    this.personalForm.patchValue({
      name: this.client.name,
      email: this.client.email,
      phoneNumber: this.client.phoneNumber,
      dateOfBirth: new Date(this.client.dateOfBirth).toISOString().split('T')[0],
      gender: this.client.gender,
      cifType: this.client.cifType
    });

    // Patch Address Form
    this.addressForm.patchValue({
      state: this.client.address.state,
      township: this.client.address.township,
      city: this.client.address.city,
      additionalAddress: this.client.address.additionalAddress || ''
    });

    // Parse and patch NRC Form
    const nrcData = this.parseNrcString(this.client.nrc);
    this.nrcForm.patchValue(nrcData);

    // Set photo preview
    if (this.client.photo) {
      this.photoPreview = this.client.photo;
    }

    // Set NRC photo previews
    if (this.client.nrcFrontPhoto) {
      this.nrcFrontPhotoPreview = this.client.nrcFrontPhoto;
    }
    if (this.client.nrcBackPhoto) {
      this.nrcBackPhotoPreview = this.client.nrcBackPhoto;
    }

    // Update filtered townships based on selected state
    this.filterNrcTownships();
  }

  private loadExistingData() {
    // Load existing emails and phone numbers
    this.cifService.getAllUniqueEmails().subscribe(emails => {
      this.existingEmails = emails;
      // Remove current client's email from validation check
      this.existingEmails.delete(this.client.email);
    });

    this.cifService.getAllUniquePhoneNumbers().subscribe(phones => {
      this.existingPhoneNumbers = phones;
      // Remove current client's phone number from validation check
      this.existingPhoneNumbers.delete(this.client.phoneNumber);
    });
  }

  private loadTownshipData() {
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        this.townshipData = data;
        this.states = Object.keys(this.townshipData);
        
        // Load initial townships based on client's state
        if (this.client.address.state && this.townshipData[this.client.address.state]) {
          this.townships = Object.keys(this.townshipData[this.client.address.state]);
          
          // Load initial cities based on client's township
          if (this.client.address.township) {
            const cities = this.townshipData[this.client.address.state][this.client.address.township];
            if (cities) {
              this.cities = cities;
            }
          }
        }
      },
      error: (error) => {
        console.error('Error loading townships:', error);
        this.toastr.error('Failed to load address data');
      }
    });
  }

  private loadNrcData() {
    this.nrcStateNumbers = this.nrcService.getNrcStateNumbers();
    this.nrcTypes = this.nrcService.getNrcTypes();
    
    this.nrcService.getNrcTownships().subscribe({
      next: (townships) => {
        this.nrcTownships = townships;
        this.filterNrcTownships();
      },
      error: (error) => {
        console.error('Error loading NRC townships:', error);
        this.toastr.error('Failed to load NRC data');
      }
    });
  }

  private filterNrcTownships() {
    const selectedState = this.nrcForm?.get('stateNumber')?.value;
    if (selectedState) {
      this.filteredNrcTownships = this.nrcTownships.filter(
        township => township.nrc_code === selectedState
      );
    } else {
      this.filteredNrcTownships = [];
    }
  }

  private duplicateEmailValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return this.cifService.getAllUniqueEmails().pipe(
        map(emails => {
          // Remove current client's email from the check
          emails.delete(this.client.email);
          const isDuplicate = emails.has(control.value);
          return isDuplicate ? { duplicateEmail: true } : null;
        })
      );
    };
  }

  private duplicatePhoneValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return this.cifService.getAllUniquePhoneNumbers().pipe(
        map(phones => {
          // Remove current client's phone number from the check
          phones.delete(this.client.phoneNumber);
          const isDuplicate = phones.has(control.value);
          return isDuplicate ? { duplicatePhone: true } : null;
        })
      );
    };
  }

  onStateChange() {
    const selectedState = this.addressForm.get('state')?.value;
    if (selectedState && this.townshipData[selectedState]) {
      this.townships = Object.keys(this.townshipData[selectedState]);
      
      // Only reset township and city if the state has changed
      if (selectedState !== this.client.address.state) {
        this.addressForm.patchValue({
          township: '',
          city: ''
        });
        this.cities = [];
      }
    }
  }

  onTownshipChange() {
    const selectedState = this.addressForm.get('state')?.value;
    const selectedTownship = this.addressForm.get('township')?.value;
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.cities = cities;
      }
      
      // Only reset city if the township has changed
      if (selectedTownship !== this.client.address.township) {
        this.addressForm.patchValue({
          city: ''
        });
      }
    }
  }

  async onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.profilePhoto = file;
      this.photoPreview = URL.createObjectURL(file);
      this.personalForm.patchValue({ photo: file });
    }
  }

  async onNrcPhotoSelected(event: any, type: 'front' | 'back') {
    const file = event.target.files[0];
    if (file) {
      if (type === 'front') {
        this.nrcFrontFile = file;
        this.nrcFrontPhotoPreview = URL.createObjectURL(file);
        this.nrcForm.patchValue({ nrcFrontPhoto: file });
      } else {
        this.nrcBackFile = file;
        this.nrcBackPhotoPreview = URL.createObjectURL(file);
        this.nrcForm.patchValue({ nrcBackPhoto: file });
      }
    }
  }

  async onSubmit() {
    if (this.personalForm.valid && this.addressForm.valid && this.nrcForm.valid) {
      this.loading = true;
      this.error = null;

      try {
        let photoUrl = this.client.photo;
        let nrcFrontUrl = this.client.nrcFrontPhoto;
        let nrcBackUrl = this.client.nrcBackPhoto;

        // Upload profile photo if changed
        if (this.profilePhoto) {
          const uploadResult = await this.cloudinaryService.uploadImage(this.profilePhoto).toPromise();
          if (uploadResult) {
            photoUrl = uploadResult.secure_url;
          }
        }

        // Upload NRC front photo if changed
        if (this.nrcFrontFile) {
          const uploadResult = await this.cloudinaryService.uploadImage(this.nrcFrontFile).toPromise();
          if (uploadResult) {
            nrcFrontUrl = uploadResult.secure_url;
          }
        }

        // Upload NRC back photo if changed
        if (this.nrcBackFile) {
          const uploadResult = await this.cloudinaryService.uploadImage(this.nrcBackFile).toPromise();
          if (uploadResult) {
            nrcBackUrl = uploadResult.secure_url;
          }
        }

        // Format NRC string
        const nrc = `${this.nrcForm.value.stateNumber}/${this.nrcForm.value.townshipCode}(${this.nrcForm.value.type})${this.nrcForm.value.number}`;

        const updatedClient = {
          ...this.personalForm.value,
          photo: photoUrl,
          nrcFrontPhoto: nrcFrontUrl,
          nrcBackPhoto: nrcBackUrl,
          address: this.addressForm.value,
          nrc: nrc
        };

        await this.cifService.updateCIF(this.client.id, updatedClient).toPromise();
        this.toastr.success('Client updated successfully');
        this.activeModal.close(true);
      } catch (error) {
        console.error('Error updating client:', error);
        this.error = 'Failed to update client';
        this.toastr.error(this.error);
      } finally {
        this.loading = false;
      }
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.personalForm.controls).forEach(key => {
        const control = this.personalForm.get(key);
        control?.markAsTouched();
      });
      Object.keys(this.addressForm.controls).forEach(key => {
        const control = this.addressForm.get(key);
        control?.markAsTouched();
      });
      Object.keys(this.nrcForm.controls).forEach(key => {
        const control = this.nrcForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  dismiss() {
    this.activeModal.dismiss();
  }

  isPersonalFieldInvalid(fieldName: string): boolean {
    const field = this.personalForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  isAddressFieldInvalid(fieldName: string): boolean {
    const field = this.addressForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  isNrcFieldInvalid(fieldName: string): boolean {
    const field = this.nrcForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getPersonalErrorMessage(fieldName: string): string {
    const control = this.personalForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
      if (control.errors['email']) {
        return 'Please enter a valid email address';
      }
      if (control.errors['duplicateEmail']) {
        return 'This email is already registered';
      }
      if (control.errors['duplicatePhone']) {
        return 'This phone number is already registered';
      }
    }
    return '';
  }

  getAddressErrorMessage(fieldName: string): string {
    const control = this.addressForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
    }
    return '';
  }

  getNrcErrorMessage(fieldName: string): string {
    const control = this.nrcForm.get(fieldName);
    if (control?.errors) {
      if (control.errors['required']) {
        return 'This field is required';
      }
      if (control.errors['minlength'] || control.errors['maxlength']) {
        return 'NRC number must be 6 digits';
      }
      if (control.errors['pattern']) {
        return 'Only numbers are allowed';
      }
    }
    return '';
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.photoPreview) URL.revokeObjectURL(this.photoPreview);
    if (this.nrcFrontPhotoPreview) URL.revokeObjectURL(this.nrcFrontPhotoPreview);
    if (this.nrcBackPhotoPreview) URL.revokeObjectURL(this.nrcBackPhotoPreview);
  }
} 