import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false,
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  showLockoutModal = false;
  remainingTime = '10:00';
  private countdownInterval: any;

  
 

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.loginForm = this.formBuilder.group({
      userCode: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }
  private startCountdown(duration: number) {
    let remaining = duration;
    
    this.updateTimeDisplay(remaining);
    
    this.countdownInterval = setInterval(() => {
      remaining -= 1000;
      
      if (remaining <= 0) {
        clearInterval(this.countdownInterval);
        this.showLockoutModal = false;
        this.authService.resetAttempts();
        return;
      }
      
      this.updateTimeDisplay(remaining);
    }, 1000);
  }

  get f() { return this.loginForm.controls; }
  private updateTimeDisplay(milliseconds: number) {
    const minutes = Math.floor(milliseconds / 60000);
    const seconds = Math.floor((milliseconds % 60000) / 1000);
    this.remainingTime = 
      `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }

  // Add ngOnDestroy to clean up interval
  ngOnDestroy() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  
  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    // Check if account is blocked
    if (this.authService.isAccountBlocked()) {
      const remaining = this.authService.getRemainingLockTime();
      this.showLockoutMessage(remaining);
      return;
    }

    this.loading = true;
    this.authService.login(this.loginForm.value)
      .subscribe({
        next: (response) => {
          this.authService.resetAttempts();
          this.toastr.success('Login successful');
          this.router.navigate(['/settings/profile']);
        },
        error: (error) => {
          this.authService.recordFailedAttempt();
          
          // Check if account is now blocked after this attempt
          if (this.authService.isAccountBlocked()) {
            const remaining = this.authService.getRemainingLockTime();
            this.showLockoutMessage(remaining);
          } else {
            const attempts = parseInt(localStorage.getItem('failedAttempts') || '0', 10);
            const remainingAttempts = this.authService.MAX_ATTEMPTS - attempts;
            let errorMessage = error.error?.message || 'Login failed';
            errorMessage += ` (${remainingAttempts} attempts remaining)`;
            this.toastr.error(errorMessage);
          }
        
          this.loading = false;
        }
      });
  }

  private showLockoutMessage(remainingTime: number) {
    // Set modal visibility to true
    this.showLockoutModal = true;
  
    const minutes = Math.floor(remainingTime / 60000);
    const seconds = Math.floor((remainingTime % 60000) / 1000);
    
    // Optionally, you might still want to show a toastr message
    this.toastr.error(
      `Account locked due to too many failed attempts. Please try again in ${minutes}m ${seconds}s`,
      'Account Locked',
      { timeOut: 0, extendedTimeOut: 0 }
    );
    
    // Optionally start the countdown if you want the modal to update its timer
    this.startCountdown(remainingTime);
  
    this.loading = false;
  }
  
}