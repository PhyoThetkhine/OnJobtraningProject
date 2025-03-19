export interface Transaction {
  id: number;
  fromAccountId: number;
  toAccountId: number;
  fromAccountType: string;
  toAccountType: string;
  amount: number;
  paymentMethod: {
    id: number;
    paymentType: string;
    createdDate: string;
    updatedDate: string;
  };
  transactionDate: string;
}

export enum AccountType {
  CIF = 'CIF',
  BRANCH = 'BRANCH'
}

export interface TransactionResponse {
  content: Transaction[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
  };
}