import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin, firstValueFrom } from 'rxjs';
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

  // Custom validators
  private phoneNumberPattern = /^(09|959|\+959)\d{7,9}$/;
  private emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

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
  ) {
    this.createForm();
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
      email: ['', [Validators.required, Validators.pattern(this.emailPattern)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(this.phoneNumberPattern)]],
      photo: [''],
      photoFile: [''],
      nrcState: ['', Validators.required],
      nrcTownship: ['', Validators.required],
      nrcType: ['', Validators.required],
      nrcNumber: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
      nrcFrontPhoto: [''],  // Not required in form validation
      nrcBackPhoto: [''],   // Not required in form validation
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      branchId: ['', Validators.required],
      roleId: ['', Validators.required],
      address: this.fb.group({
        state: ['', Validators.required],
        township: ['', Validators.required],
        city: ['', Validators.required],
        additionalAddress: ['']
      })
      
    });

    // Debug form state changes
    this.userForm.valueChanges.subscribe(() => {
      console.log('Form Valid:', this.userForm.valid);
      if (!this.userForm.valid) {
        this.debugFormValidation();
      }
    });
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
        
        // Set default branch if user is not from Main Branch
        if (this.currentUser && this.currentUser.branchName !== 'Main Branch') {
          const userBranch = this.branches.find(
            branch => branch.branchName === this.currentUser?.branchName
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
      if (control.errors['pattern']) {
        if (fieldName === 'phoneNumber') {
          return 'Please enter a valid Myanmar phone number (e.g., 09123456789)';
        }
        if (fieldName === 'nrcNumber') {
          return 'Please enter 6 digits';
        }
        return 'Please enter a valid format';
      }
    }
    return '';
  }

  // Add this method to debug address data
  private debugAddressData() {
    console.log('Current Address Data:', {
      states: this.states,
      selectedState: this.userForm.get('address.state')?.value,
      townships: this.townships,
      selectedTownship: this.userForm.get('address.township')?.value,
      cities: this.cities,
      selectedCity: this.userForm.get('address.city')?.value,
      townshipData: this.townshipData
    });
  }

  // Add this method to debug form validation
  public debugFormValidation() {
    console.log('Form Validation State:', {
      formValid: this.userForm.valid,
      formErrors: this.userForm.errors,
      formValue: this.userForm.value,
      controls: {
        name: this.userForm.get('name')?.errors,
        email: this.userForm.get('email')?.errors,
        phoneNumber: this.userForm.get('phoneNumber')?.errors,
        nrcState: this.userForm.get('nrcState')?.errors,
        nrcTownship: this.userForm.get('nrcTownship')?.errors,
        nrcType: this.userForm.get('nrcType')?.errors,
        nrcNumber: this.userForm.get('nrcNumber')?.errors,
        dateOfBirth: this.userForm.get('dateOfBirth')?.errors,
        gender: this.userForm.get('gender')?.errors,
        branchId: this.userForm.get('branchId')?.errors,
        roleId: this.userForm.get('roleId')?.errors,
        address: {
          state: this.userForm.get('address.state')?.errors,
          township: this.userForm.get('address.township')?.errors,
          city: this.userForm.get('address.city')?.errors
        }
      }
    });
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
}
