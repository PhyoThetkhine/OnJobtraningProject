import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { Role, AUTHORITY } from '../../../models/role.model';
import { RolePermission } from '../../../models/role-permission.model';
import { User } from '../../../models/user.model';
import { RoleService } from '../../../services/role.service';
import { UserService } from '../../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { Permission } from '../../../models/permission.model';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalComponent } from '../../../shared/components/confirmation-modal/confirmation-modal.component';

@Component({
  selector: 'app-role-detail',
  templateUrl: './role-detail.component.html',
  styleUrls: ['./role-detail.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule]
})
export class RoleDetailComponent implements OnInit {
  roleId!: number;
  role: Role | null = null;
  users: User[] = [];
  permissions: RolePermission[] = [];
  loading = false;
  error: string | null = null;
  activeTab: 'staff' | 'permissions' = 'staff';

  // Pagination for users
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  Math = Math;

  AUTHORITY = AUTHORITY;

  // Add new properties
  availablePermissions: Permission[] = [];
  addingPermission = false;
  removingPermission = false;

  // Add these properties
  isEditing = false;
  roleForm: FormGroup = this.fb.group({
    roleName: ['', [Validators.required, Validators.minLength(3)]],
    authority: [AUTHORITY.RegularBranchLevel, Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private roleService: RoleService,
    private userService: UserService,
    private toastr: ToastrService,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.roleId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadRoleDetails();
    this.loadRoleUsers();
    this.loadRolePermissions();
    this.loadAvailablePermissions();
  }

  loadRoleDetails() {
    this.loading = true;
    this.roleService.getRoleById(this.roleId).subscribe({
      next: (role) => {
        this.role = role;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading role:', error);
        this.toastr.error('Failed to load role details');
        this.loading = false;
      }
    });
  }

  loadRoleUsers() {
    this.roleService.getRoleUsers(this.roleId, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.users = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
      },
      error: (error) => {
        console.error('Error loading role users:', error);
        this.toastr.error('Failed to load users');
      }
    });
  }

  loadRolePermissions() {
    this.roleService.getRolePermissions(this.roleId).subscribe({
      next: (permissions) => {
        this.permissions = permissions;
        // Refresh available permissions after loading current ones
        this.loadAvailablePermissions();
      },
      error: (error) => {
        console.error('Error loading role permissions:', error);
        this.toastr.error('Failed to load permissions');
      }
    });
  }

  loadAvailablePermissions() {
    this.roleService.getAllPermissions().subscribe({
      next: (permissions) => {
        // Get IDs of currently assigned permissions
        const assignedPermissionIds = new Set(
          this.permissions.map(p => p.permission.id)
        );
        
        // Filter to only show permissions that aren't already assigned
        this.availablePermissions = permissions.filter(permission => 
          !assignedPermissionIds.has(permission.id)
        );
      },
      error: (error) => {
        console.error('Error loading available permissions:', error);
        this.toastr.error('Failed to load available permissions');
      }
    });
  }

  addPermission(permissionId: number) {
    this.addingPermission = true;
    this.roleService.addRolePermission(this.roleId, permissionId).subscribe({
      next: () => {
        this.toastr.success('Permission added successfully');
        // Reload permissions and available permissions
        this.loadRolePermissions();
        this.loadAvailablePermissions();
        this.addingPermission = false;
      },
      error: (error) => {
        console.error('Error adding permission:', error);
        this.toastr.error('Failed to add permission');
        this.addingPermission = false;
      }
    });
  }

  removePermission(permissionId: number, permissionName: string) {
    const modalRef = this.modalService.open(ConfirmationModalComponent, {
      centered: true
    });
    
    modalRef.componentInstance.title = 'Remove Permission';
    modalRef.componentInstance.message = 
      `Are you sure you want to remove "${permissionName}" permission from this role?`;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.removingPermission = true;
          this.roleService.removeRolePermission(this.roleId, permissionId).subscribe({
            next: () => {
              this.toastr.success('Permission removed successfully');
              this.loadRolePermissions();
              this.loadAvailablePermissions();
              this.removingPermission = false;
            },
            error: (error) => {
              console.error('Error removing permission:', error);
              this.toastr.error('Failed to remove permission');
              this.removingPermission = false;
            }
          });
        }
      },
      () => {} // Modal dismissed
    );
  }

  setActiveTab(tab: 'staff' | 'permissions') {
    this.activeTab = tab;
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadRoleUsers();
  }

  getAuthorityBadgeClass(authority: AUTHORITY): string {
    return authority === AUTHORITY.MainBranchLevel ? 'bg-primary' : 'bg-info';
  }

  startEditing() {
    this.isEditing = true;
    this.roleForm.patchValue({
      roleName: this.role?.roleName,
      authority: this.role?.authority
    });
  }

  cancelEditing() {
    this.isEditing = false;
    this.roleForm.reset();
  }

  updateRole() {
    if (this.roleForm.valid && this.role) {
      this.loading = true;
      const roleData = this.roleForm.value;

      this.roleService.updateRole(this.roleId, roleData).subscribe({
        next: (updatedRole) => {
          this.role = updatedRole;
          this.isEditing = false;
          this.loading = false;
          this.toastr.success('Role updated successfully');
        },
        error: (error) => {
          console.error('Error updating role:', error);
          this.toastr.error(error.error?.message || 'Failed to update role');
          this.loading = false;
        }
      });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.roleForm.get(fieldName);
    return field ? (field.invalid && (field.dirty || field.touched)) : false;
  }
} 