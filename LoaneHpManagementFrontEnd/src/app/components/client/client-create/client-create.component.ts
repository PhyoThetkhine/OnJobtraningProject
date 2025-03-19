import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors, AsyncValidatorFn } from '@angular/forms';
import { NrcService, NrcTownship } from '../../../services/nrc.service';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { BranchService } from '../../../services/branch.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CIFService, Gender, ClientRegistrationDTO, BusinessRegistrationDTO, AddressDTO, AccountDTO, FinancialInfoDTO } from '../../../services/cif.service';
import { CommonModule } from '@angular/common';
import { CIFType, CIF } from '../../../models/cif.model';
import { debounceTime, distinctUntilChanged, map, switchMap, takeUntil } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-client-create',
  standalone: false,
  templateUrl: './client-create.component.html',
  styleUrl: './client-create.component.css'
})
export class ClientCreateComponent implements OnInit, OnDestroy {
  currentStep = 1;
  totalSteps = 3;
  clientForm!: FormGroup;
  businessForm!: FormGroup;
  accountForm!: FormGroup;
  nrcStateNumbers: string[] = [];
  nrcTownships: NrcTownship[] = [];
  nrcTypes: string[] = [];
  filteredTownships: NrcTownship[] = [];
 
  loading = false;
  error: string | null = null;
  townshipData: any = {};
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  businessPhotos: { file: File, preview: string }[] = [];
  CIFType = CIFType;

  // Add new properties for photo previews
  photoPreview: string | null = null;
  nrcFrontPhotoPreview: string | null = null;
  nrcBackPhotoPreview: string | null = null;

  // Add new properties for business address
  businessStates: string[] = [];
  businessTownships: string[] = [];
  businessCities: string[] = [];

  private destroy$ = new Subject<void>();
  private existingEmails: Set<string> = new Set();
  private existingPhoneNumbers: Set<string> = new Set();

  // Store actual files
  private profilePhoto: File | null = null;
  private nrcFrontFile: File | null = null;
  private nrcBackFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private cifService: CIFService,
    private nrcService: NrcService,
    private cloudinaryService: CloudinaryService,
    private branchService: BranchService,
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.loadExistingData();
    this.createForms();
  }

  ngOnInit() {
    this.loadInitialData();
  }

  private loadExistingData() {
    // Load existing emails and phone numbers
    this.cifService.getAllUniqueEmails().subscribe(emails => {
      this.existingEmails = emails;
    });

    this.cifService.getAllUniquePhoneNumbers().subscribe(phones => {
      this.existingPhoneNumbers = phones;
    });
  }

  private createForms() {
    // Client Information Form with async validators
    this.clientForm = this.fb.group({
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
      nrcState: ['', Validators.required],
      nrcTownship: ['', Validators.required],
      nrcType: ['', Validators.required],
      nrcNumber: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      cifType: ['', Validators.required],
      photo: ['', Validators.required],
      address: this.fb.group({
        state: ['', Validators.required],
        township: ['', Validators.required],
        city: ['', Validators.required],
        additionalAddress: ['']
      }),
      nrcFrontPhoto: ['', Validators.required],
      nrcBackPhoto: ['', Validators.required]
    });

    // Add value change subscriptions for live validation
    this.clientForm.get('email')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.clientForm.get('email')?.updateValueAndValidity();
    });

    this.clientForm.get('phoneNumber')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.clientForm.get('phoneNumber')?.updateValueAndValidity();
    });

    // Business Information Form
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

    // Account Setup Form
    this.accountForm = this.fb.group({
      minAmount: [0, [Validators.required, Validators.min(0)]],
      maxAmount: [0, [Validators.required, Validators.min(0)]],
    
    });
  }

  private loadInitialData() {
    this.loading = true;
    
    // Get static data
    this.nrcStateNumbers = this.nrcService.getNrcStateNumbers();
    this.nrcTypes = this.nrcService.getNrcTypes();

    // Load NRC townships
    this.nrcService.getNrcTownships().subscribe({
      next: (townships) => {
        console.log('NRC Townships loaded:', townships);
        this.nrcTownships = townships;
      },
      error: (error) => {
        console.error('Error loading NRC townships:', error);
      }
    });

    // // Load branches
    // this.branchService.getAllBranches().subscribe({
    //   next: (branches) => {
    //     console.log('Branches loaded:', branches);
    //     this.branches = branches;
    //   },
    //   error: (error) => {
    //     console.error('Error loading branches:', error);
    //   }
    // });

    // Load address townships
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        console.log('Address Townships loaded:', data);
        if (data) {
          this.townshipData = data;
          this.states = Object.keys(this.townshipData);
          this.businessStates = [...this.states]; // Copy states for business use
        }
      },
      error: (error) => {
        console.error('Error loading address townships:', error);
      }
    });

    this.loading = false;
  }

  onNrcStateChange() {
    const stateNumber = this.clientForm.get('nrcState')?.value;
    if (stateNumber) {
      this.filteredTownships = this.nrcTownships.filter(
        township => township.nrc_code === stateNumber
      );
      this.clientForm.patchValue({ nrcTownship: '' });
    } else {
      this.filteredTownships = [];
    }
  }

  // Client Address Methods
  onStateChange() {
    const selectedState = this.clientForm.get('address.state')?.value;
    if (selectedState && this.townshipData[selectedState]) {
      this.townships = Object.keys(this.townshipData[selectedState]);
      this.clientForm.patchValue({
        address: {
          ...this.clientForm.get('address')?.value,
          township: '',
          city: ''
        }
      });
      this.cities = [];
    }
  }

  onTownshipChange() {
    const selectedState = this.clientForm.get('address.state')?.value;
    const selectedTownship = this.clientForm.get('address.township')?.value;
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.cities = cities;
      }
      
      this.clientForm.patchValue({
        address: {
          ...this.clientForm.get('address')?.value,
          city: ''
        }
      });
    }
  }

  // Business Address Methods
  onBusinessStateChange() {
    const selectedState = this.businessForm.get('address.state')?.value;
    if (selectedState && this.townshipData[selectedState]) {
      this.businessTownships = Object.keys(this.townshipData[selectedState]);
      this.businessForm.patchValue({
        address: {
          ...this.businessForm.get('address')?.value,
          township: '',
          city: ''
        }
      });
      this.businessCities = [];
    }
  }

  onBusinessTownshipChange() {
    const selectedState = this.businessForm.get('address.state')?.value;
    const selectedTownship = this.businessForm.get('address.township')?.value;
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.businessCities = cities;
      }
      
      this.businessForm.patchValue({
        address: {
          ...this.businessForm.get('address')?.value,
          city: ''
        }
      });
    }
  }

  nextStep() {
    if (this.currentStep === 1 && this.clientForm.valid) {
      console.log('Step 1 validation passed');
      if (this.clientForm.get('cifType')?.value === CIFType.PERSONAL) {
        this.currentStep = 3;
      } else {
        this.currentStep = 2;
      }
    } else if (this.currentStep === 2) {
      console.log('Business Form Validation:', this.businessForm.valid);
      console.log('Business Form Errors:', this.businessForm.errors);
      console.log('Business Form Value:', this.businessForm.value);
      
      // Check specific form controls
      Object.keys(this.businessForm.controls).forEach(key => {
        const control = this.businessForm.get(key);
        if (control?.errors) {
          console.log(`${key} errors:`, control.errors);
        }
      });

      if (this.businessForm.valid) {
        this.currentStep = 3;
      } else {
        // Mark all fields as touched to show validation errors
        Object.keys(this.businessForm.controls).forEach(key => {
          const control = this.businessForm.get(key);
          control?.markAsTouched();
        });

        // For nested form groups (address and financial)
        const addressControls = this.businessForm.get('address') as FormGroup;
        if (addressControls) {
          Object.keys(addressControls.controls).forEach(key => {
            const control = addressControls.get(key);
            control?.markAsTouched();
          });
        }

        const financialControls = this.businessForm.get('financial') as FormGroup;
        if (financialControls) {
          Object.keys(financialControls.controls).forEach(key => {
            const control = financialControls.get(key);
            control?.markAsTouched();
          });
        }
      }
    }
  }

  previousStep() {
    if (this.currentStep === 3) {
      if (this.clientForm.get('cifType')?.value === CIFType.PERSONAL) {
        this.currentStep = 1;
      } else {
        this.currentStep = 2;
      }
    } else if (this.currentStep === 2) {
      this.currentStep = 1;
    }
  }

  private formatDate(date: string | Date): string {
    if (date instanceof Date) {
      return date.toISOString().split('T')[0];
    }
    return date;
  }

  async onSubmit() {
    if (this.clientForm.valid && 
        this.accountForm.valid && 
        (this.clientForm.get('cifType')?.value === CIFType.PERSONAL || this.businessForm.valid)) {
      
      this.loading = true;
      this.error = null;

      try {
        // Upload all photos to Cloudinary first
        const uploadTasks = [];
        let photoUrl, nrcFrontUrl, nrcBackUrl;

        if (this.profilePhoto) {
          uploadTasks.push(
            this.cloudinaryService.uploadImage(this.profilePhoto)
              .toPromise()
              .then(response => photoUrl = response.secure_url)
          );
        }

        if (this.nrcFrontFile) {
          uploadTasks.push(
            this.cloudinaryService.uploadImage(this.nrcFrontFile)
              .toPromise()
              .then(response => nrcFrontUrl = response.secure_url)
          );
        }

        if (this.nrcBackFile) {
          uploadTasks.push(
            this.cloudinaryService.uploadImage(this.nrcBackFile)
              .toPromise()
              .then(response => nrcBackUrl = response.secure_url)
          );
        }

        // Upload business photos if any
        let businessPhotoUrls: string[] = [];
        if (this.businessPhotos.length > 0) {
          const businessUploadTasks = this.businessPhotos.map(photo =>
            this.cloudinaryService.uploadImage(photo.file)
              .toPromise()
              .then(response => businessPhotoUrls.push(response.secure_url))
          );
          uploadTasks.push(...businessUploadTasks);
        }

        // Wait for all uploads to complete
        await Promise.all(uploadTasks);

        const formData = this.clientForm.value;
        const selectedTownship = this.filteredTownships.find(
          (t: NrcTownship) => t.name_en === formData.nrcTownship
        );
        
        if (!selectedTownship) {
          throw new Error('NRC Township not found');
        }

        // Format NRC string properly
        const nrc = `${formData.nrcState}/${selectedTownship.name_en}(${formData.nrcType})${formData.nrcNumber}`;
        
        // Get current user ID
        const currentUser = await this.authService.getCurrentUser().toPromise();
        if (!currentUser) {
          throw new Error('No authenticated user found');
        }

        // Validate NRC data before proceeding
        if (!formData.nrcState || !formData.nrcTownship || !formData.nrcType || !formData.nrcNumber) {
          throw new Error('NRC information is incomplete');
        }

        // Prepare the client data matching backend DTO structure
        const clientData: ClientRegistrationDTO = {
          name: formData.name,
          email: formData.email,
          phoneNumber: formData.phoneNumber,
          nrc: nrc, // Make sure NRC is properly formatted
          photo: photoUrl || '',
          nrcFrontPhoto: nrcFrontUrl || '',
          nrcBackPhoto: nrcBackUrl || '',
          dateOfBirth: new Date(formData.dateOfBirth),
          gender: formData.gender as Gender,
          cifType: formData.cifType,
          address: {
            state: formData.address.state,
            township: formData.address.township,
            city: formData.address.city,
            additionalAddress: formData.address.additionalAddress || ''
          },
          status: 13,
          account: {
            minAmount: Number(this.accountForm.value.minAmount),
            maxAmount: Number(this.accountForm.value.maxAmount)
          },
          createdUserId: currentUser.id
        };

        // Add business data if not personal
        if (formData.cifType !== CIFType.PERSONAL) {
          const businessFormData = this.businessForm.value;
          const businessData: BusinessRegistrationDTO = {
            name: businessFormData.name,
            companyType: businessFormData.companyType,
            category: businessFormData.category,
            businessType: businessFormData.businessType,
            registrationDate: new Date(businessFormData.registrationDate),
            licenseNumber: businessFormData.licenseNumber,
            licenseIssueDate: new Date(businessFormData.licenseIssueDate),
            licenseExpiryDate: new Date(businessFormData.licenseExpiryDate),
            phoneNumber: businessFormData.phoneNumber,
            address: {
              state: businessFormData.address.state,
              township: businessFormData.address.township,
              city: businessFormData.address.city,
              additionalAddress: businessFormData.address.additionalAddress || ''
            },
            businessPhotos: businessPhotoUrls,
            financial: {
              averageIncome: Number(businessFormData.financial.averageIncome),
              expectedIncome: Number(businessFormData.financial.expectedIncome),
              averageExpenses: Number(businessFormData.financial.averageExpenses),
              averageInvestment: Number(businessFormData.financial.averageInvestment),
              averageEmployees: Number(businessFormData.financial.averageEmployees),
              averageSalaryPaid: Number(businessFormData.financial.averageSalaryPaid),
              revenueProof: businessFormData.financial.revenueProof
            },
            createdUserId: currentUser.id
          };
          clientData.business = businessData;
        }

        // Log the NRC value before sending
        console.log('Sending NRC:', clientData.nrc);

        // Send the data to backend
        await this.cifService.createCIF(clientData).toPromise();
        this.router.navigate(['/clients']);
      } catch (error) {
        console.error('Error creating client:', error);
        this.error = 'Failed to create client. Please try again.';
      } finally {
        this.loading = false;
      }
    } else {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.clientForm.controls).forEach(key => {
        const control = this.clientForm.get(key);
        control?.markAsTouched();
      });
      
      if (this.clientForm.get('cifType')?.value !== CIFType.PERSONAL) {
        Object.keys(this.businessForm.controls).forEach(key => {
          const control = this.businessForm.get(key);
          control?.markAsTouched();
        });
      }
      
      Object.keys(this.accountForm.controls).forEach(key => {
        const control = this.accountForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  async onFileSelected(event: any, type: 'front' | 'back' | 'profile') {
    const file = event.target.files[0];
    if (file) {
      // Store the file and create preview only
      const previewUrl = URL.createObjectURL(file);
      
      switch (type) {
        case 'front':
          this.nrcFrontFile = file;
          this.nrcFrontPhotoPreview = previewUrl;
          this.clientForm.patchValue({ nrcFrontPhoto: file }); // Store file temporarily
          break;
        case 'back':
          this.nrcBackFile = file;
          this.nrcBackPhotoPreview = previewUrl;
          this.clientForm.patchValue({ nrcBackPhoto: file }); // Store file temporarily
          break;
        case 'profile':
          this.profilePhoto = file;
          this.photoPreview = previewUrl;
          this.clientForm.patchValue({ photo: file }); // Store file temporarily
          break;
      }
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

  isFieldInvalid(fieldName: string, formType: 'client' | 'business' | 'account' = 'client'): boolean {
    let form: FormGroup;
    switch (formType) {
      case 'business':
        form = this.businessForm;
        break;
      case 'account':
        form = this.accountForm;
        break;
      default:
        form = this.clientForm;
    }
    const field = form.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }

  getErrorMessage(fieldName: string, formType: 'client' | 'business' | 'account' = 'client'): string {
    let form: FormGroup;
    switch (formType) {
      case 'business':
        form = this.businessForm;
        break;
      case 'account':
        form = this.accountForm;
        break;
      default:
        form = this.clientForm;
    }
    const control = form.get(fieldName);
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
      if (control.errors['pattern']) {
        return 'Please enter a valid format';
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

  private duplicateEmailValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return this.cifService.getAllUniqueEmails().pipe(
        map(emails => {
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
          const isDuplicate = phones.has(control.value);
          return isDuplicate ? { duplicatePhone: true } : null;
        })
      );
    };
  }

  // Add cleanup method for previews
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // Revoke object URLs to prevent memory leaks
    if (this.photoPreview) URL.revokeObjectURL(this.photoPreview);
    if (this.nrcFrontPhotoPreview) URL.revokeObjectURL(this.nrcFrontPhotoPreview);
    if (this.nrcBackPhotoPreview) URL.revokeObjectURL(this.nrcBackPhotoPreview);
    this.businessPhotos.forEach(photo => URL.revokeObjectURL(photo.preview));
  }
}
