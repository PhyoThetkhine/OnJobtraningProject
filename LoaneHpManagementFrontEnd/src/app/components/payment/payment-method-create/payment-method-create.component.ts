import { Component, EventEmitter, Output } from '@angular/core';
import { PaymentMethod } from 'src/app/models/payment-method.model';
import { PaymentMethodService } from 'src/app/services/payment-method.service';

@Component({
  selector: 'app-payment-method-create',
  standalone: false,
  templateUrl: './payment-method-create.component.html',
  styleUrl: './payment-method-create.component.css'
})
export class PaymentMethodCreateComponent {
  paymentMethod: Partial<PaymentMethod> = { paymentType: '' };
  @Output() paymentMethodCreated = new EventEmitter<void>();


  successMessage: string = '';
  errorMessage: string = '';

  constructor(private paymentMethodService: PaymentMethodService) {}

  createPaymentMethod(): void {
    this.successMessage = '';
    this.errorMessage = '';

    const paymentType = this.paymentMethod.paymentType?.trim();

    if (!paymentType) {
      this.errorMessage = 'Payment type is required.';
      return;
    }

    // Step 1: Check if the payment method already exists
    this.paymentMethodService.checkPaymentMethodExists(paymentType).subscribe({
      next: (exists) => {
        if (exists) {
          this.errorMessage = 'Payment method already exists.';
          return;
        }

        // Step 2: If it doesn’t exist, proceed with creation
        this.paymentMethodService.createPaymentMethod(this.paymentMethod).subscribe({
          next: (response) => {
            this.successMessage = response.message || 'Payment method created successfully';
            this.paymentMethod = { paymentType: '' }; // Reset form
            this.paymentMethodCreated.emit(); // Notify parent component
            this.clearMessages();
          },
          error: (err) => {
            console.error('Error:', err);
            this.errorMessage = err.message || 'Error creating payment method';
            this.clearMessages();
          },
        });
      },
      error: (err) => {
        console.error('Error checking payment method:', err);
        this.errorMessage = 'Error validating payment method';
      },
    });
  }

  // Optional: Clear messages after a delay
  clearMessages(): void {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000); // Clear after 3 seconds
  }

}