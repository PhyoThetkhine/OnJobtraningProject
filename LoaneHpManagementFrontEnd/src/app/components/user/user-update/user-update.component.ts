import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors, AsyncValidatorFn } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from '../../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../../models/user.model';
import { HttpClient } from '@angular/common/http';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { CommonModule } from '@angular/common';
import { Observable, Subject } from 'rxjs';
import { map, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { NrcService, NrcTownship } from '../../../services/nrc.service';


@Component({
  selector: 'app-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class UserUpdateComponent implements OnInit, OnDestroy {
  @Input() user!: User;
  personalForm!: FormGroup;
  addressForm!: FormGroup;
  nrcForm!: FormGroup;
  loading = false;
  error: string | null = null;
  townshipData: any = {};
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  
  
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
    private userService: UserService,
    private toastr: ToastrService,
    private http: HttpClient,
    private cloudinaryService: CloudinaryService,
    private nrcService: NrcService,
 
  ) {}

  ngOnInit() {
    if (this.user) {
      console.log('User data received:', this.user);
      console.log('Current NRC:', this.user.nrc);
      
      // Set initial photo previews
      this.photoPreview = this.user.photo || null;
      this.nrcFrontPhotoPreview = this.user.nrcFrontPhoto || null;
      this.nrcBackPhotoPreview = this.user.nrcBackPhoto || null;
    }
    
    // First load the data needed for the forms
    this.loadNrcData();
    this.loadTownshipData();
    this.loadExistingData();
    
    // Initialize forms
    this.initializeForms();
    
    // Then patch the forms with user data
    this.patchFormsWithUserData();
  }

  private parseNrcString(nrcString: string): { stateNumber: string, townshipCode: string, type: string, number: string } {
    console.log('Parsing NRC string:', nrcString);
    
    try {
      if (!nrcString) {
        console.log('Empty NRC string');
        return { stateNumber: '', townshipCode: '', type: '', number: '' };
      }
      
      const parts = nrcString.split('/');
      if (parts.length !== 2) {
        console.log('Invalid NRC format - wrong number of parts');
        throw new Error('Invalid NRC format');
      }

      const stateNumber = parts[0];
      const matches = parts[1].match(/([A-Za-z]+)\(([A-Za-z]+)\)(\d+)/);
      
      if (!matches) {
        console.log('Invalid NRC format - could not parse second part');
        throw new Error('Invalid NRC format');
      }

      const result = {
        stateNumber: stateNumber,
        townshipCode: matches[1],
        type: matches[2],
        number: matches[3]
      };
      
      console.log('Successfully parsed NRC:', result);
      return result;
    } catch (error) {
      console.error('Error parsing NRC:', error);
      console.log('NRC String that caused error:', nrcString);
      return {
        stateNumber: '',
        townshipCode: '',
        type: '',
        number: ''
      };
    }
  }

  private initializeForms() {
    console.log('Initializing forms with user data:', this.user);
    
    // Initialize Personal Information Form
    this.personalForm = this.fb.group({
      name: [this.user?.name || '', Validators.required],
      email: [this.user?.email || '', {
        validators: [Validators.required, Validators.email],
        asyncValidators: [this.duplicateEmailValidator()],
        updateOn: 'blur'
      }],
      phoneNumber: [this.user?.phoneNumber || '', {
        validators: [Validators.required],
        asyncValidators: [this.duplicatePhoneValidator()],
        updateOn: 'blur'
      }],
      dateOfBirth: [this.user?.dateOfBirth ? new Date(this.user.dateOfBirth).toISOString().split('T')[0] : '', Validators.required],
      gender: [this.user?.gender || '', Validators.required],
      photo: [null]
    });

    // Initialize Address Form
    this.addressForm = this.fb.group({
      state: [this.user?.address?.state || '', Validators.required],
      township: [this.user?.address?.township || '', Validators.required],
      city: [this.user?.address?.city || '', Validators.required],
      additionalAddress: [this.user?.address?.additionalAddress || '']
    });

    // Parse and initialize NRC Form
    console.log('Current NRC:', this.user?.nrc);
    const nrcData = this.parseNrcString(this.user?.nrc || '');
    console.log('Parsed NRC Data:', nrcData);
    
    // Initialize NRC form with validators but mark as pristine
    this.nrcForm = this.fb.group({
      stateNumber: [nrcData.stateNumber || '', Validators.required],
      townshipCode: [nrcData.townshipCode || '', Validators.required],
      type: [nrcData.type || '', Validators.required],
      number: [nrcData.number || '', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(6),
        Validators.pattern('^[0-9]*$')
      ]],
      nrcFrontPhoto: [''],
      nrcBackPhoto: ['']
    });

    // If we have valid NRC data, mark the form as pristine and untouched
    if (this.user?.nrc) {
      Object.keys(this.nrcForm.controls).forEach(key => {
        const control = this.nrcForm.get(key);
        if (control && control.value) {
          control.markAsPristine();
          control.markAsUntouched();
        }
      });
    }

    // Log initial form values
    console.log('Initial NRC Form Values:', this.nrcForm.value);
    console.log('NRC Form Valid:', this.nrcForm.valid);
    console.log('NRC Form Pristine:', this.nrcForm.pristine);

    // Set up form value change subscriptions
    this.setupFormSubscriptions();
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

  private patchFormsWithUserData() {
    if (!this.user) {
      console.log('No user data available for patching');
      return;
    }

    console.log('Patching forms with user data:', this.user);

    // Patch Personal Form
    this.personalForm.patchValue({
      name: this.user.name,
      email: this.user.email,
      phoneNumber: this.user.phoneNumber,
      dateOfBirth: new Date(this.user.dateOfBirth).toISOString().split('T')[0],
      gender: this.user.gender
    });

    // Patch Address Form
    this.addressForm.patchValue({
      state: this.user.address.state,
      township: this.user.address.township,
      city: this.user.address.city,
      additionalAddress: this.user.address.additionalAddress || ''
    });

    // Parse and patch NRC Form
    const nrcData = this.parseNrcString(this.user.nrc);
    console.log('Parsed NRC data for form:', nrcData);
    
    this.nrcForm.patchValue({
      stateNumber: nrcData.stateNumber,
      townshipCode: nrcData.townshipCode,
      type: nrcData.type,
      number: nrcData.number
    });

    console.log('NRC Form values after patch:', this.nrcForm.value);

    // Set photo preview
    if (this.user.photo) {
      this.photoPreview = this.user.photo;
    }

    // Set NRC photo previews
    if (this.user.nrcFrontPhoto) {
      this.nrcFrontPhotoPreview = this.user.nrcFrontPhoto;
    }
    if (this.user.nrcBackPhoto) {
      this.nrcBackPhotoPreview = this.user.nrcBackPhoto;
    }

    // Update filtered townships based on selected state
    this.filterNrcTownships();
  }

  private loadExistingData() {
    // Load existing emails and phone numbers
    this.userService.getAllUniqueEmails().subscribe(emails => {
      this.existingEmails = emails;
      // Remove current user's email from validation check
      this.existingEmails.delete(this.user.email);
    });

    this.userService.getAllUniquePhoneNumbers().subscribe(phones => {
      this.existingPhoneNumbers = phones;
      // Remove current user's phone number from validation check
      this.existingPhoneNumbers.delete(this.user.phoneNumber);
    });
  }

  private loadTownshipData() {
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        this.townshipData = data;
        this.states = Object.keys(this.townshipData);
        
        // Load initial townships based on user's state
        if (this.user.address.state && this.townshipData[this.user.address.state]) {
          this.townships = Object.keys(this.townshipData[this.user.address.state]);
          
          // Load initial cities based on user's township
          if (this.user.address.township) {
            const cities = this.townshipData[this.user.address.state][this.user.address.township];
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
      return this.userService.getAllUniqueEmails().pipe(
        map(emails => {
          // Remove current user's email from the check
          emails.delete(this.user.email);
          const isDuplicate = emails.has(control.value);
          return isDuplicate ? { duplicateEmail: true } : null;
        })
      );
    };
  }

  private duplicatePhoneValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return this.userService.getAllUniquePhoneNumbers().pipe(
        map(phones => {
          // Remove current user's phone number from the check
          phones.delete(this.user.phoneNumber);
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
      if (selectedState !== this.user.address.state) {
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
      if (selectedTownship !== this.user.address.township) {
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
      // Revoke previous preview URL to prevent memory leaks
      if (this.photoPreview && this.photoPreview.startsWith('blob:')) {
        URL.revokeObjectURL(this.photoPreview);
      }
      this.photoPreview = URL.createObjectURL(file);
      this.personalForm.patchValue({ photo: file });
    }
  }

  async onNrcPhotoSelected(event: any, type: 'front' | 'back') {
    const file = event.target.files[0];
    if (file) {
      if (type === 'front') {
        this.nrcFrontFile = file;
        // Revoke previous preview URL
        if (this.nrcFrontPhotoPreview && this.nrcFrontPhotoPreview.startsWith('blob:')) {
          URL.revokeObjectURL(this.nrcFrontPhotoPreview);
        }
        this.nrcFrontPhotoPreview = URL.createObjectURL(file);
        this.nrcForm.patchValue({ nrcFrontPhoto: file });
      } else {
        this.nrcBackFile = file;
        // Revoke previous preview URL
        if (this.nrcBackPhotoPreview && this.nrcBackPhotoPreview.startsWith('blob:')) {
          URL.revokeObjectURL(this.nrcBackPhotoPreview);
        }
        this.nrcBackPhotoPreview = URL.createObjectURL(file);
        this.nrcForm.patchValue({ nrcBackPhoto: file });
      }
    }
  }

  async onSubmit() {
    console.log('Form Status:', {
      personal: {
        valid: this.personalForm.valid,
        errors: this.personalForm.errors,
        touched: this.personalForm.touched,
        dirty: this.personalForm.dirty
      },
      address: {
        valid: this.addressForm.valid,
        errors: this.addressForm.errors,
        touched: this.addressForm.touched,
        dirty: this.addressForm.dirty
      },
      nrc: {
        valid: this.nrcForm.valid,
        errors: this.nrcForm.errors,
        touched: this.nrcForm.touched,
        dirty: this.nrcForm.dirty,
        values: this.nrcForm.value
      }
    });

    // Check if forms are valid (including pristine forms with valid data)
    const isValid = this.personalForm.valid && 
                   this.addressForm.valid && 
                   (this.nrcForm.valid || (this.nrcForm.pristine && this.user?.nrc));

    if (isValid) {
      this.loading = true;
      this.error = null;

      try {
        let photoUrl = this.user.photo;
        let nrcFrontUrl = this.user.nrcFrontPhoto;
        let nrcBackUrl = this.user.nrcBackPhoto;

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

        const updatedUser = {
          name: this.personalForm.value.name,
          email: this.personalForm.value.email,
          phoneNumber: this.personalForm.value.phoneNumber,
          dateOfBirth: this.personalForm.value.dateOfBirth,
          gender: this.personalForm.value.gender,
          photo: photoUrl,
          nrcFrontPhoto: nrcFrontUrl,
          nrcBackPhoto: nrcBackUrl,
          address: this.addressForm.value,
          nrc: nrc
        };

        await this.userService.updateUser(this.user.id, updatedUser).toPromise();
        this.toastr.success('User updated successfully');
        this.activeModal.close(true);
      } catch (error) {
        console.error('Error updating user:', error);
        this.error = 'Failed to update user';
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
    
    // Clean up object URLs
    if (this.photoPreview && this.photoPreview.startsWith('blob:')) {
      URL.revokeObjectURL(this.photoPreview);
    }
    if (this.nrcFrontPhotoPreview && this.nrcFrontPhotoPreview.startsWith('blob:')) {
      URL.revokeObjectURL(this.nrcFrontPhotoPreview);
    }
    if (this.nrcBackPhotoPreview && this.nrcBackPhotoPreview.startsWith('blob:')) {
      URL.revokeObjectURL(this.nrcBackPhotoPreview);
    }
  }
} 