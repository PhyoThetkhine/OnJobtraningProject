import { User } from './user.model';

export enum PaymentMethodStatus {
  ACTIVE = 'active',
  DELETED = 'deleted'
}

export interface PaymentMethod {
  id: number;
  paymentType: string;
  createdDate: string;
  updatedDate: string;
  createdUser: User;
  status: PaymentMethodStatus;
}

export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
} 