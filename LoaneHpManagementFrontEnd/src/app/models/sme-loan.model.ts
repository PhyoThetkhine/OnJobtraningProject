import { User } from './user.model';
import { CIF } from './cif.model';

export enum FREQUENCY {
  MONTHLY = 'MONTHLY',
  YEARLY = 'YEARLY'
}

export interface SMETerm {
  id: number;
  principal: number;
  interest: number;
  dueDate: Date;
  days: number;
  interestOfOverdue: number;
  interestLateDays: number;
  interestLateFee: number;
  totalRepaymentAmount: number;
  outstandingAmount: number;
  lastRepayment: number;
  lastRepayDate: Date | null;
  status: string;
}

export interface SMELoan {
  id: number;
  smeLoanCode: string;
  purpose: string;
  frequency: FREQUENCY;
  loanAmount: number;
  disbursementAmount: number;
  interestRate: number;
  gracePeriod: number;
  applicationDate: Date;
  startDate?: Date;
  endDate?: Date;
  lateFeeRate: number;
  defaultedRate: number;
  longTermOverdueRate: number;
  duration: number;
  status: string;
  documentFeeRate: number;
  documentFee: number;
  serviceChargeFeeRate: number;
  serviceCharge: number;
  updatedDate: Date;
  confirmDate?: Date;
  cif: CIF;
  createdUser: User;
  confirmUser?: User;
  terms?: SMETerm[];
  paidPrincipalStatus: 'paid' | 'not_paid';
} 