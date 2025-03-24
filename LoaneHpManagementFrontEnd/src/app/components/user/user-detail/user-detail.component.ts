import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { UserService } from '../../../services/user.service';
import { UserCurrentAccountService } from '../../../services/user-current-account.service';
import { LoadingDelayService } from '../../../services/loading-delay.service';
import { User, ApiResponse } from '../../../models/user.model';
import { Role } from '../../../models/role.model';
import { UserCurrentAccount, FreezeStatus } from '../../../models/user-current-account.model';
import { RoleService } from '../../../services/role.service';
import { TransactionService } from '../../../services/transaction.service';
import { Transaction, TransactionResponse } from '../../../models/transaction.model';
import { AuthService } from '../../../services/auth.service';
import { UserPermission } from '../../../models/permission.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalComponent } from '../../../shared/components/confirmation-modal/confirmation-modal.component';
import { UserUpdateComponent } from '../user-update/user-update.component';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css'],
  standalone: true,
  imports: [CommonModule, ConfirmationModalComponent, UserUpdateComponent]
})
export class UserDetailComponent implements OnInit {
  userId!: number;
  user: User | null = null;
  userAccount: UserCurrentAccount | null = null;
  loading = false;
  error: string | null = null;
  activeTab: 'profile' | 'documents' | 'balance' | 'permissions' = 'profile';
  showImageViewer = false;
  selectedImage: string | null = null;
  roles: Role[] = [];
  userAccountId: number | null = null;
  transactions: Transaction[] = [];
  currentPage = 0;
  pageSize = 3;
  totalElements = 0;
  totalPages = 0;
  FreezeStatus = FreezeStatus;
  refreshing = false;
  Math = Math;
  transactionLoading = false;
  permissions: UserPermission[] = [];
  loadingPermissions = false;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private userAccountService: UserCurrentAccountService,
    private roleService: RoleService,
    private toastr: ToastrService,
    private loadingDelayService: LoadingDelayService,
    private transactionService: TransactionService,
    private authService: AuthService,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUserDetails();
    this.loadRoles();
    
    this.loadUserPermissions();
  }

  getUserStatusText(status: string): string {
    return status;
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'terminated':
        return 'bg-danger';
      case 'retired':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }

  loadUserDetails() {
    this.loading = true;
   
    this.userService.getUserById(this.userId).subscribe({
      next: (user) => {
        this.user = user;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading user:', error);
        this.error = 'Failed to load user details';
        this.loading = false;
      }
    });
  }

  public hasPermission(permission: string): boolean {
    return this.authService.hasPermission(permission);
  }

  loadRoles() {
    this.loading = true;
    this.roleService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.loading = false;
        console.log('Loaded roles:', roles);
      },
      error: (error) => {
        console.error('Error loading roles:', error);
        this.toastr.error('Failed to load roles', 'Error');
        this.loading = false;
      }
    });
  }

  setActiveTab(tab: 'profile' | 'documents' | 'balance' | 'permissions') {
    this.activeTab = tab;
    if (tab === 'balance') {
     
    }
    if (tab === 'permissions' && this.permissions.length === 0) {
      this.loadUserPermissions();
    }
  }


  changeStatus(newStatus: 'active' | 'terminated' | 'retired') {
    if (this.user && this.user.status !== newStatus) {
      this.loading = true;
      this.loadingDelayService.addDelay(
        this.userService.changeUserStatus(this.user.id, newStatus)
      ).subscribe({
        next: (updatedUser) => {
          setTimeout(() => {
            if (this.user) {
              this.user = {
                ...this.user,
                status: newStatus
              };
            }
            this.loading = false;
            
            const statusMessage = {
              'active': 'User has been activated successfully',
              'retired': 'User has been retired successfully',
              'terminated': 'User has been terminated successfully'
            }[newStatus] || 'Status updated successfully';
            
            this.toastr.success(statusMessage, 'Status Change', {
              timeOut: 3000,
              closeButton: true,
              progressBar: true
            });

          }, 500);
        },
        error: (error) => {
          this.error = 'Failed to change user status';
          this.loading = false;
          console.error('Error changing status:', error);
          
          this.toastr.error(
            `Failed to change status to ${newStatus}`, 
            'Status Change Failed',
            {
              timeOut: 4000,
              closeButton: true,
              progressBar: true
            }
          );
        }
      });
    }
  }

  changeRole(roleId: number) {
    if (this.loading || !this.user) return;
    
    this.loading = true;
    
    this.userService.updateUserRole(this.user.id, roleId).subscribe({
      next: () => {
        this.loadingDelayService.addDelay(
          this.userService.getUserById(this.user!.id)
        ).subscribe({
          next: (updatedUser) => {
            setTimeout(() => {
              this.user = updatedUser;
              this.loading = false;
              this.toastr.success('Role updated successfully', 'Success', {
                timeOut: 3000,
                closeButton: true,
                progressBar: true
              });
              this.loadUserDetails();
              this.loadUserPermissions();
            
            }, 300);
          },
          error: (error) => {
            console.error('Error reloading user:', error);
            this.loading = false;
            this.toastr.error('Failed to reload user data', 'Error', {
              timeOut: 4000,
              closeButton: true,
              progressBar: true
            });
          }
        });
      },
      error: (error) => {
        console.error('Error updating role:', error);
        this.loading = false;
        this.toastr.error('Failed to update role', 'Error', {
          timeOut: 4000,
          closeButton: true,
          progressBar: true
        });
      }
    });
  }

  openImageViewer(imageUrl: string) {
    this.selectedImage = imageUrl;
    this.showImageViewer = true;
  }

  closeImageViewer() {
    this.showImageViewer = false;
    this.selectedImage = null;
  }

  previousPage() {
    if (this.currentPage > 0) {
      
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
     
    }
  }

  refreshBalance() {
    this.refreshing = true;
    this.loadingDelayService.addDelay(
      this.userAccountService.getAccountByUserId(this.userId)
    ).subscribe({
      next: (account) => {
        this.userAccount = account;
        this.toastr.success('Balance refreshed successfully');
      },
      error: (error) => {
        console.error('Error refreshing balance:', error);
        this.toastr.error('Failed to refresh balance');
      },
      complete: () => {
        setTimeout(() => {
          this.refreshing = false;
        }, 500);
      }
    });
  }

  changeFreezeStatus(status: FreezeStatus) {
    if (!this.userAccount) return;
    
    this.loading = true;
    this.loadingDelayService.addDelay(
      this.userAccountService.changeFreezeStatus(this.userAccount.id, status)
    ).subscribe({
      next: (updatedAccount) => {
        this.userAccount = updatedAccount;
        const message = status === FreezeStatus.NOT_FREEZE 
          ? 'Account unfrozen successfully'
          : 'Account frozen successfully';
        this.toastr.success(message);
      
      },
      error: (error) => {
        console.error('Error changing freeze status:', error);
        this.toastr.error('Failed to change account status');
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  loadUserPermissions() {
    if (!this.user?.id) return;
    
    this.loadingPermissions = true;
    this.userService.getUserPermissions(this.user.id).subscribe({
      next: (response) => {
        this.permissions = response.data;
        this.loadingPermissions = false;
      },
      error: (error) => {
        console.error('Error loading user permissions:', error);
        this.toastr.error('Failed to load user permissions');
        this.loadingPermissions = false;
      }
    });
  }

  togglePermission(permission: UserPermission) {
    const newStatus = permission.isAllowed === 'allow' ? 'limited' : 'allow';
    
    const modalRef = this.modalService.open(ConfirmationModalComponent, {
      centered: true
    });
    
    modalRef.componentInstance.title = 'Change Permission Status';
    modalRef.componentInstance.message = 
      `Are you sure you want to ${newStatus === 'allow' ? 'allow' : 'limit'} this permission?`;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.userService.updateUserPermission(
            permission.id.userId,
            permission.id.permissionId,
            newStatus
          ).subscribe({
            next: (response) => {
              permission.isAllowed = newStatus;
              this.toastr.success(`Permission ${newStatus === 'allow' ? 'allowed' : 'limited'}`);
            },
            error: (error) => {
              console.error('Error updating permission:', error);
              this.toastr.error('Failed to update permission');
            }
          });
        }
      },
      () => {} // Modal dismissed
    );
  }

  openUpdateModal() {
    if (!this.user) return;

    const modalRef = this.modalService.open(UserUpdateComponent, {
      size: 'lg',
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.user = this.user;

    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadUserDetails();
        }
      },
      () => {} // Modal dismissed
    );
  }

} 