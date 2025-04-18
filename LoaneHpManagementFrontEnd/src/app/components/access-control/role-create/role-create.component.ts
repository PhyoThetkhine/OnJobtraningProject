import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Role, AUTHORITY } from '../../../models/role.model';
import { Permission } from '../../../models/permission.model';
import { RoleService } from '../../../services/role.service';
import { catchError, map, Observable, of, switchMap, timer } from 'rxjs';

@Component({
  selector: 'app-role-create',
  templateUrl: './role-create.component.html',
  styleUrls: ['./role-create.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class RoleCreateComponent implements OnInit {
  roleForm: FormGroup;
  loading = false;
  permissions: Permission[] = [];
  AUTHORITY = AUTHORITY;

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.roleForm = this.fb.group({
      roleName: ['', 
        [Validators.required, Validators.minLength(3)],
        [this.duplicateRoleValidator()]
      ],
      authority: [AUTHORITY.RegularBranchLevel, Validators.required],
      permissions: [[], Validators.required]
    });
  }

  ngOnInit() {
    this.loadPermissions();
  }

  private duplicateRoleValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value || control.value.length < 3) {
        return of(null);
      }
      return timer(500).pipe(
        switchMap(() => this.roleService.getRoles()),
        map((roles: Role[]) => {
          const inputName = this.normalizeRoleName(control.value);
          const isDuplicate = roles.some(role => 
            this.normalizeRoleName(role.roleName) === inputName
          );
          return isDuplicate ? { duplicateRole: true } : null;
        }),
        catchError(() => of(null))
      );
    };
  }

  loadPermissions() {
    this.loading = true;
    this.roleService.getAllPermissions().subscribe({
      next: (permissions) => {
        this.permissions = permissions;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading permissions:', error);
        this.toastr.error('Failed to load permissions');
        this.loading = false;
      }
    });
  }

  onPermissionToggle(permissionId: number) {
    const currentPermissions = this.roleForm.get('permissions')?.value || [];
    const index = currentPermissions.indexOf(permissionId);
    
    if (index === -1) {
      currentPermissions.push(permissionId);
    } else {
      currentPermissions.splice(index, 1);
    }
    
    this.roleForm.patchValue({ permissions: currentPermissions });
  }

  isPermissionSelected(permissionId: number): boolean {
    const permissions = this.roleForm.get('permissions')?.value || [];
    return permissions.includes(permissionId);
  }

  onSubmit() {
    if (this.roleForm.valid) {
      this.loading = true;
      const roleData = this.roleForm.value;

      this.roleService.createRole(roleData).subscribe({
        next: (response) => {
          this.toastr.success('Role created successfully');
          this.router.navigate(['/roles']);
        },
        error: (error) => {
          console.error('Error creating role:', error);
          this.toastr.error(error.error?.message || 'Failed to create role');
          this.loading = false;
        }
      });
    } else {
      Object.keys(this.roleForm.controls).forEach(key => {
        const control = this.roleForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.roleForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched) && !field.pending) : false;
  }
  private normalizeRoleName(name: string): string {
    return name.replace(/\s+/g, '').toLowerCase();
  }
}
