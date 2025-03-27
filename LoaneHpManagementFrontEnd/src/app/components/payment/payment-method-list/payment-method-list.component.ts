import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PaymentMethod, PaymentMethodStatus } from '../../../models/payment-method.model';
import { PaymentMethodService } from '../../../services/payment-method.service';
import { ToastrService } from 'ngx-toastr';
import { ApiResponse } from '../../../models/user.model';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaymentMethodEditComponent } from '../payment-method-edit/payment-method-edit.component';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-payment-method-list',
  standalone: false,
  templateUrl: './payment-method-list.component.html',
  styleUrls: ['./payment-method-list.component.css']
})
export class PaymentMethodListComponent implements OnInit {
  paymentMethods: PaymentMethod[] = [];
  loading = true;
  error: string | null = null;
  PaymentMethodStatus = PaymentMethodStatus; // Make enum available in template
  userMap: Map<number, User> = new Map();
  showCreateForm = false;
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  pages: number[] = [];

  constructor(
    private paymentMethodService: PaymentMethodService,
    private userService: UserService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadPaymentMethods();
  }

  loadPaymentMethods(page: number = 0): void {
    this.loading = true;
    this.error = null;
    
    this.paymentMethodService.getAllPaymentMethods(page, this.pageSize).subscribe({
      next: (response: ApiResponse<any>) => {
        if (response.status === 200 && response.data) {
          this.paymentMethods = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
          this.currentPage = page;
          this.calculatePages();
          
          // Load users for payment methods where createdUser is just an ID
          this.paymentMethods.forEach(method => {
            if (typeof method.createdUser === 'number') {
              this.loadUser(method.createdUser);
            }
          });
        }
        this.loading = false;
      },
      error: (error: any) => {
        this.error = 'Failed to load payment methods. Please try again later.';
        this.loading = false;
        console.error('Error loading payment methods:', error);
      }
    });
  }

  loadUser(userId: number): void {
    if (this.userMap.has(userId)) return;

    this.userService.getUserById(userId).subscribe({
      next: (user: User) => {
        this.userMap.set(userId, user);
      },
      error: (error: any) => {
        console.error('Error loading user:', error);
      }
    });
  }

  getUser(createdUser: User | number): User | null {
    if (typeof createdUser === 'number') {
      return this.userMap.get(createdUser) || null;
    }
    return createdUser;
  }

  calculatePages(): void {
    this.pages = Array.from({length: this.totalPages}, (_, i) => i);
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.loadPaymentMethods(page);
    }
  }

  getStatusBadgeClass(status: PaymentMethodStatus): string {
    switch (status) {
      case PaymentMethodStatus.ACTIVE:
        return 'bg-success';
      case PaymentMethodStatus.DELETED:
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  openEditModal(method?: PaymentMethod): void {
    const modalRef = this.modalService.open(PaymentMethodEditComponent, {
      size: 'lg',
      backdrop: 'static'
    });

    modalRef.componentInstance.isNew = !method;
    modalRef.componentInstance.paymentMethod = method ? {...method} : undefined;

    modalRef.result.then(
      (result: PaymentMethod) => {
        if (result) {
          if (result.id) {
            this.updatePaymentMethod(result);
          } else {
            this.createPaymentMethod(result);
          }
        }
      },
      () => {} // Modal dismissed
    );
  }

  createPaymentMethod(method: PaymentMethod): void {
    this.paymentMethodService.createPaymentMethod(method).subscribe({
      next: (response: ApiResponse<PaymentMethod>) => {
        if (response.status === 200) {
          this.toastr.success('Payment method created successfully');
          this.loadPaymentMethods(this.currentPage);
        }
      },
      error: (error: Error) => {
        this.toastr.error('Failed to create payment method');
        console.error('Error creating payment method:', error);
      }
    });
  }

  updatePaymentMethod(method: PaymentMethod): void {
    this.paymentMethodService.updatePaymentMethod(method.id, method).subscribe({
      next: (response: ApiResponse<PaymentMethod>) => {
        if (response.status === 200) {
          this.toastr.success('Payment method updated successfully');
          this.loadPaymentMethods(this.currentPage);
        }
      },
      error: (error: Error) => {
        this.toastr.error('Failed to update payment method');
        console.error('Error updating payment method:', error);
      }
    });
  }
  onPaymentMethodCreated(): void {
    this.loadPaymentMethods(this.currentPage);
    this.showCreateForm = false; // Hide form after creation
  }

  deletePaymentMethod(method: PaymentMethod): void {
    this.paymentMethodService.deletePaymentMethod(method.id).subscribe({
      next: (response: ApiResponse<void>) => {
        if (response.status === 200) {
          method.status = PaymentMethodStatus.DELETED;
          this.toastr.success('Payment method deleted successfully');
          this.loadPaymentMethods(this.currentPage);
        }
      },
      error: (error: any) => {
        this.toastr.error('Failed to delete payment method');
        console.error('Error deleting payment method:', error);
      }
    });
  }
  
  reactivatePaymentMethod(method: PaymentMethod): void {
    this.paymentMethodService.reactivatePaymentMethod(method.id).subscribe({
      next: (response: ApiResponse<void>) => {
        if (response.status === 200) {
          method.status = PaymentMethodStatus.ACTIVE;
          this.toastr.success('Payment method reactivated successfully');
          this.loadPaymentMethods(this.currentPage);
        }
      },
      error: (error: any) => {
        this.toastr.error('Failed to reactivate payment method');
        console.error('Error reactivating payment method:', error);
      }
    });
  }

  canDelete(method: PaymentMethod): boolean {
    return method.status === PaymentMethodStatus.ACTIVE;
  }

  canActivate(method: PaymentMethod): boolean {
    return method.status === PaymentMethodStatus.DELETED;
  }
}
