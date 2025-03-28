import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { LoadingDelayService } from '../../../services/loading-delay.service';
import { CurrentUser, User } from '../../../models/user.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserUpdateComponent } from '../../user/user-update/user-update.component';
import { switchMap } from 'rxjs';
import { UserPermission } from 'src/app/models/permission.model';


@Component({
  selector: 'app-your-profile',
  templateUrl: './your-profile.component.html',
  styleUrls: ['./your-profile.component.css'],
  standalone: true,
  imports: [CommonModule, UserUpdateComponent]
})
export class YourProfileComponent implements OnInit {
  user: User | null = null;
  loginUser : CurrentUser |null = null;
  loading = true;
  error: string | null = null;
  activeTab: 'profile' | 'documents' | 'balance' | 'permissions' = 'profile';
  showImageViewer = false;
  selectedImage: string | null = null;
    permissions: UserPermission[] = [];
    loadingPermissions = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private toastr: ToastrService,
    private loadingDelay: LoadingDelayService,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.loading = true;
    this.loadingDelay.addDelay(
      this.authService.getCurrentUser().pipe(
        // Fetch detailed user data after getting the current user
        switchMap(loginUser => {
          this.loginUser = loginUser; // Optional: store basic info
          return this.userService.getUserById(loginUser.id);
          
        })
        
      )
    ).subscribe({
      next: (detailedUser) => {
        this.user = detailedUser; // Store detailed user data
        this.loading = false;
    this.loadUserPermissions();

      },
      error: (err) => {
        console.error('Failed to load profile:', err);
        this.error = 'Failed to load profile data';
        this.loading = false;
        this.toastr.error('Failed to load profile information', 'Error');
      }
    });
  }

  hasPermission(permission: string): boolean {
    return this.authService.hasPermission(permission);
  }

  openUpdateModal() {
    if (!this.user || !this.hasPermission('Update Staff Info')) return;

    const modalRef = this.modalService.open(UserUpdateComponent, {
      size: 'lg',
      centered: true,
      backdrop: 'static'
    });

    modalRef.componentInstance.user = { ...this.user };

    modalRef.result.then((result) => {
      if (result === 'updated') {
        this.toastr.success('Profile updated successfully');
        this.loadCurrentUser();
      }
    }).catch(() => {});
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


  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active': return 'bg-success';
      case 'terminated': return 'bg-danger';
      case 'retired': return 'bg-warning';
      default: return 'bg-secondary';
    }
  }

  openImageViewer(imageUrl: string) {
    this.selectedImage = imageUrl;
    this.showImageViewer = true;
  }

  closeImageViewer() {
    this.showImageViewer = false;
    this.selectedImage = null;
  }

  setActiveTab(tab: 'profile' | 'documents' | 'balance'|'permissions') {
    this.activeTab = tab;
  }
}