import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, ReactiveFormsModule, AsyncValidatorFn, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin, firstValueFrom, Subject, Observable, map } from 'rxjs';
import { UserService } from '../../../services/user.service';
import { NrcService, NrcTownship } from '../../../services/nrc.service';
import { CloudinaryService } from '../../../services/cloudinary.service';
import { BranchService } from '../../../services/branch.service';
import { Branch } from '../../../models/branch.model';
import { Gender } from '../../../models/common.types';
import { HttpClient } from '@angular/common/http';
import { RoleService } from '../../../services/role.service';
import { Role } from '../../../models/role.model';
import { CreateUserDto, CurrentUser } from '../../../models/user.model';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { AuthService } from 'src/app/services/auth.service';
import { AUTHORITY } from 'src/app/models/role.model';
import { CIFService } from 'src/app/services/cif.service';

@Component({
  selector: 'app-user-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-create.component.html',
  styleUrl: './user-create.component.css'
})
export class UserCreateComponent implements OnInit {
  userForm!: FormGroup;
  nrcStateNumbers: string[] = [];
  nrcTownships: NrcTownship[] = [];
  nrcTypes: string[] = [];
  filteredTownships: NrcTownship[] = [];
  branches: Branch[] = [];
  loading = false;
  error: string | null = null;
  townshipData: any = {};
  states: string[] = [];
  townships: string[] = [];
  cities: string[] = [];
  roles: Role[] = [];
  currentUser: CurrentUser | null = null;

  // Image preview properties
  profileImagePreview: string | null = null;
  nrcFrontPreview: string | null = null;
  nrcBackPreview: string | null = null;
  
private destroy$ = new Subject<void>();
  private existingEmails: Set<string> = new Set();
  private existingPhoneNumbers: Set<string> = new Set();
  // Custom validators
  private phoneNumberPattern = /^(09|959|\+959)\d{7,9}$/;
  private emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  maxDate: Date;
  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private nrcService: NrcService,
    private cloudinaryService: CloudinaryService,
    private branchService: BranchService,
    private router: Router,
    private http: HttpClient,
    private roleService: RoleService,
    private toastr: ToastrService,
    private authService:AuthService,
        private cifService: CIFService,
        
  ) {
    this.createForm();
    const today = new Date();
  this.maxDate = new Date(
    today.getFullYear() - 18,
    today.getMonth(),
    today.getDate()
  );
  }

  ngOnInit() {
    this.loadInitialData();
    
  }

  private createForm() {
    this.userForm = this.fb.group({
      createdUser: this.fb.group({
        id: [this.currentUser?.id || 0] // Initialize with current user ID
      }),
      name: ['', [Validators.required, Validators.minLength(3)]],
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
      photo: [''],
      photoFile: [''],
      nrcState: ['', Validators.required],
      nrcTownship: ['', Validators.required],
      nrcType: ['', Validators.required],
      nrcNumber: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
      nrcFrontPhoto: [''],  // Not required in form validation
      nrcBackPhoto: [''],   // Not required in form validation
      dateOfBirth: ['', Validators.required,this.minAgeValidator(18)],
      gender: ['', Validators.required],
      branchId: ['', Validators.required,],
      roleId: ['', Validators.required],
      address: this.fb.group({
        state: ['', Validators.required],
        township: ['', Validators.required],
        city: ['', Validators.required],
        additionalAddress: ['']
      })
      
    });

   
  }
  // Add this method to your component class
private minAgeValidator(minAge: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }
    
    const birthDate = new Date(control.value);
    const today = new Date();
    
    // Set hours to 0 to compare dates without time influence
    today.setHours(0, 0, 0, 0);
    
    const minDate = new Date(
      today.getFullYear() - minAge,
      today.getMonth(),
      today.getDate()
    );

    return birthDate > minDate ? { minAge: true } : null;
  };
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
  private loadInitialData() {
    this.loading = true;
    
    // Get static data
    this.nrcStateNumbers = this.nrcService.getNrcStateNumbers();
    this.nrcTypes = this.nrcService.getNrcTypes();
    console.log('Static NRC Data:', { states: this.nrcStateNumbers, types: this.nrcTypes });

    // Load NRC townships
    this.nrcService.getNrcTownships().subscribe({
      next: (townships) => {
        console.log('NRC Townships loaded:', townships);
        if (townships && townships.length > 0) {
          this.nrcTownships = townships;
        } else {
          console.warn('No NRC townships data received');
          this.toastr.warning('No NRC townships data available');
        }
      },
      error: (error) => {
        console.error('Error loading NRC townships:', error);
        this.toastr.error('Failed to load NRC townships');
      }
    });

    // Get the current user's information first
    this.authService.getCurrentUser().subscribe({
      next: (currentUser) => {
        console.log('Current role level:', currentUser.roleLevel);
        this.currentUser = currentUser;
        this.loadBranches();
        this.loadRoles(); // Load roles after getting current user
      },
      error: (error) => {
        console.error('Error fetching current user:', error);
        this.toastr.error('Failed to fetch current user');
        this.loading = false;
      }
    });

    // Load address townships
    this.http.get<any>('assets/myanmar-townships.json').subscribe({
      next: (data) => {
        console.log('Raw Address Townships data:', data);
        if (data) {
          this.townshipData = data;
          this.states = Object.keys(this.townshipData);
          if (this.states.length === 0) {
            console.warn('No states found in township data');
            this.toastr.warning('No address data available');
          } else {
            console.log('States loaded:', this.states);
          }
        } else {
          console.warn('No address data received');
          this.toastr.warning('No address data available');
        }
      },
      error: (error) => {
        console.error('Error loading address townships:', error);
        this.toastr.error('Failed to load address townships');
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  private loadRoles() {
    // Check if current user is from regular branch level
    if (this.currentUser?.roleLevel === 'RegularBranchLevel') {
      // Load only regular branch level roles
      this.roleService.getRegularBranchLevelRoles().subscribe({
        next: (roles) => {
          console.log('Regular branch level roles loaded:', roles);
          this.roles = roles;
        },
        error: (error) => {
          console.error('Error loading roles:', error);
          this.toastr.error('Failed to load roles');
        }
      });
    } else {
      // Load all roles for main branch level users
      this.roleService.getRoles().subscribe({
        next: (roles) => {
          console.log('All roles loaded:', roles);
          this.roles = roles;
        },
        error: (error) => {
          console.error('Error loading roles:', error);
          this.toastr.error('Failed to load roles');
        }
      });
    }
  }

  onNrcStateChange() {
    const stateNumber = this.userForm.get('nrcState')?.value;
    console.log('NRC State Change:', {
      selectedState: stateNumber,
      allTownships: this.nrcTownships,
      townshipsLength: this.nrcTownships?.length
    });
    
    if (stateNumber) {
      this.filteredTownships = this.nrcTownships.filter(
        township => township.nrc_code === stateNumber
      );
      console.log('Filtered Townships:', {
        filtered: this.filteredTownships,
        count: this.filteredTownships.length,
        forState: stateNumber
      });
      
      this.userForm.patchValue({
        nrcTownship: ''
      });
    } else {
      this.filteredTownships = [];
    }
  }

  onStateChange() {
    const selectedState = this.userForm.get('address.state')?.value;
    console.log('Address State Change:', {
      selectedState,
      availableStates: this.states,
      townshipData: this.townshipData,
      hasSelectedState: Boolean(this.townshipData[selectedState])
    });
    
    if (selectedState && this.townshipData[selectedState]) {
      this.townships = Object.keys(this.townshipData[selectedState]);
      console.log('Available Townships:', {
        townships: this.townships,
        count: this.townships.length,
        forState: selectedState
      });
      
      this.userForm.patchValue({
        address: {
          ...this.userForm.get('address')?.value,
          township: '',
          city: ''
        }
      });
      this.cities = [];
    }
  }

  onTownshipChange() {
    const selectedState = this.userForm.get('address.state')?.value;
    const selectedTownship = this.userForm.get('address.township')?.value;
    console.log('Selected State:', selectedState);
    console.log('Selected Township:', selectedTownship);
    
    if (selectedState && selectedTownship) {
      const cities = this.townshipData[selectedState]?.[selectedTownship];
      if (cities) {
        this.cities = cities;
        console.log('Available Cities:', this.cities);
      }
      
      // Reset city selection
      this.userForm.patchValue({
        address: {
          ...this.userForm.get('address')?.value,
          city: ''
        }
      });
    }
  }
  private loadBranches() {
    this.branchService.getAllBranches().subscribe({
      next: (branches) => {
        console.log('Branches loaded:', branches);
        this.branches = branches;
  
        // Set default branch if user is RegularBranchLevel
        if (this.isRegularBranchUser()) {
          const userBranch = this.branches.find(
            branch => branch.id === this.currentUser?.branch?.id
          );
  
          if (userBranch) {
            console.log('Setting branch:', userBranch);
            const branchControl = this.userForm.get('branchId');
            branchControl?.setValue(userBranch.id);
            branchControl?.disable();
            
            this.toastr.info('Branch selection is locked to your assigned branch');
          }
        }
      },
      error: (error) => {
        console.error('Error loading branches:', error);
        this.toastr.error('Failed to load branches');
      }
    });
  }
  // Image preview handlers
  onProfilePhotoSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.previewImage(file, 'profile');
      this.userForm.patchValue({ photoFile: file });
    }
  }

  onNrcPhotoSelected(event: any, type: 'front' | 'back') {
    const file = event.target.files[0];
    if (file) {
      this.previewImage(file, type);
    }
  }

  private previewImage(file: File, type: 'profile' | 'front' | 'back') {
    const reader = new FileReader();
    reader.onload = () => {
      switch (type) {
        case 'profile':
          this.profileImagePreview = reader.result as string;
          break;
        case 'front':
          this.nrcFrontPreview = reader.result as string;
          break;
        case 'back':
          this.nrcBackPreview = reader.result as string;
          break;
      }
    };
    reader.readAsDataURL(file);
  }

  async onSubmit() {
    if (this.userForm.valid) {
      this.loading = true;
      try {
        // Upload images to Cloudinary
        const uploadTasks = [];
        
        if (this.userForm.get('photoFile')?.value) {
          uploadTasks.push(
            firstValueFrom(this.cloudinaryService.uploadImage(this.userForm.get('photoFile')?.value))
          );
        }
        
        const nrcFrontFile = document.querySelector<HTMLInputElement>('input[type=file][name=nrcFront]')?.files?.[0];
        const nrcBackFile = document.querySelector<HTMLInputElement>('input[type=file][name=nrcBack]')?.files?.[0];
        
        if (nrcFrontFile) uploadTasks.push(firstValueFrom(this.cloudinaryService.uploadImage(nrcFrontFile)));
        if (nrcBackFile) uploadTasks.push(firstValueFrom(this.cloudinaryService.uploadImage(nrcBackFile)));

        const uploadResults = await Promise.all(uploadTasks);

        // Prepare user data
        const formData = this.userForm.value;
        const selectedTownship = this.filteredTownships.find(
          t => t.name_en === formData.nrcTownship
        );
        
        const nrc = `${formData.nrcState}/${selectedTownship?.name_en}(${formData.nrcType})${formData.nrcNumber}`;

        // Get the branch ID - need to handle disabled control
        const branchControl = this.userForm.get('branchId');
        const branchId = branchControl?.disabled ? branchControl.value : branchControl?.value;
        
        console.log('Branch ID:', branchId); // Debug log

        if (!branchId) {
          this.toastr.error('Branch selection is required');
          this.loading = false;
          return;
        }
        
        const userData: CreateUserDto = {
          name: formData.name,
          email: formData.email,
          phoneNumber: formData.phoneNumber,
          photo: uploadResults[0]?.secure_url || '',
          nrc: nrc,
          nrcFrontPhoto: uploadResults[1]?.secure_url || '',
          nrcBackPhoto: uploadResults[2]?.secure_url || '',
          dateOfBirth: formData.dateOfBirth,
          gender: formData.gender,
          address: formData.address,
          branch: { id: parseInt(branchId.toString()) }, // Ensure branch ID is a number
          role: { id: parseInt(formData.roleId) }, // Ensure role ID is a number
          createdUser: { 
            id: this.currentUser?.id || 0 // Use current user's ID
          },
          password: ''
        };

        // Debug log
        console.log('Sending user data:', userData);

        // Validate current user
        if (!this.currentUser?.id) {
          this.toastr.error('User session expired');
          this.router.navigate(['/login']);
          return;
        }

        this.userService.createUser(userData).subscribe({
          next: () => {
            this.toastr.success('User created successfully');
            this.router.navigate(['/users']);
          },
          error: (error) => {
            console.error('Error creating user:', error);
            const errorMessage = error.error?.message || 'Failed to create user';
            this.toastr.error(errorMessage);
            this.loading = false;
          }
        });
      } catch (error) {
        console.error('Error uploading images:', error);
        this.toastr.error('Failed to upload images');
        this.loading = false;
      }
    } else {
      this.markFormGroupTouched(this.userForm);
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

  // Add validation helper methods
  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }
  getErrorMessage(fieldName: string): string {
    const control = this.userForm.get(fieldName);
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
        if (fieldName === 'phoneNumber') {
          return 'Please enter a valid Myanmar phone number (09xxxxxxxx)';
        }
        return 'Invalid format';
      }
      if (control.errors['minAge']) {
        return 'Must be at least 18 years old';
      }
    }
    return '';
  }

 
 

  private isRegularBranchUser(): boolean {
    return this.currentUser?.role?.authority === AUTHORITY.RegularBranchLevel;
  }

  // You can use this in your template to show appropriate messages
  get userAuthorityMessage(): string {
    return this.isRegularBranchUser() 
      ? 'You can only assign regular branch level roles' 
      : 'You can assign any role';
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
}
