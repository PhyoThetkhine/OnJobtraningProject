// change-password.component.ts
import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';
import { environment } from 'src/environments/environment';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  standalone : false,
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent {
  currentPassword: string = '';
  newPassword: string = '';
  confirmNewPassword: string = '';
   private apiUrl = `${environment.apiUrl}/auth`;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  onSubmit() {
    if (this.newPassword !== this.confirmNewPassword) {
      this.toastr.error('New password and confirm password do not match.', 'Error');
      return;
    }
    // First get the current user details from the authentication service
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        const payload = {
          userId: user.id,
          currentPassword: this.currentPassword,
          newPassword: this.newPassword
        };

        this.http.post(`${this.apiUrl}/change-password`, payload, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            this.toastr.success(response, 'Success');
            // Clear form fields
            this.currentPassword = '';
            this.newPassword = '';
            this.confirmNewPassword = '';
          },
          error: (error) => {
            this.toastr.error(error.error || 'Error changing password', 'Error');
          }
        });
    },
    error: () => {
      this.toastr.error('Unable to retrieve current user information.', 'Error');
    }
  });
}
// Add these methods to change-password.component.ts
passwordsMatch(): boolean {
  return this.newPassword === this.confirmNewPassword && this.confirmNewPassword !== '';
}

hasUpperCase(): boolean {
  return /[A-Z]/.test(this.newPassword);
}

hasNumber(): boolean {
  return /\d/.test(this.newPassword);
}

formValid(): boolean {
  return this.currentPassword.length > 0 &&
         this.newPassword.length >= 8 &&
         this.passwordsMatch() &&
         this.hasUpperCase() &&
         this.hasNumber();
}
}
