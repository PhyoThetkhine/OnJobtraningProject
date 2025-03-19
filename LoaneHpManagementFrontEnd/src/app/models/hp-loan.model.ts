import { User } from './user.model';
import { ApiResponse, PagedResponse } from './common.types';

export enum LOAN_STATUS {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface CIF {
  id: number;
  name: string;
  cifCode: string;
  photo: string | null;
  // Add other CIF properties as needed
}

export interface DealerProduct {
  id: number;
  name: string;
  price: number;
  description: string;
  // Add other product properties as needed
}

export interface HpTerm {
  id: number;
  principal: number;
  interest: number;
  dueDate: string;
  days: number;
  interestOfOverdue: number;
  interestLateDays: number;
  totalRepaymentAmount: number;
  principalOfOverdue: number;
  latePrincipalDays: number;
  lastRepayment: number;
  status: string;
}

export interface HpLoanApplication {
  cifId: number;
  productId: number;
  loanAmount: number;
  downPayment: number;
  duration: number;
  createdUserId: number | null;
  status: LOAN_STATUS;
}

export interface HpLoan {
  id: number;
  hpLoanCode: string;
  loanAmount: number;
  disbursementAmount: number | null;
  downPayment: number;
  interestRate: number | null;
  gracePeriod: number | null;
  applicationDate: Date;
  startDate: Date | null;
  endDate: Date | null;
  lateFeeRate: number | null;
  defaultedRate: number | null;
  longTermOverdueRate: number | null;
  duration: number;
  status: string;
  documentFeeRate: number | null;
  documentFee: number | null;
  serviceChargeFeeRate: number | null;
  serviceCharge: number | null;
  updatedDate: Date;
  confirmDate: Date | null;
  cif: CIF;
  createdUser: User;
  confirmUser: User;
  product: DealerProduct;
  terms?: HpTerm[];
}

export interface CreateHpLoanDto extends HpLoanApplication {
  // Additional fields if needed for creation
}

export type HpLoanResponse = ApiResponse<PagedResponse<HpLoan>>; 