import { BranchCurrentAccount } from './branch-account.model';

export interface CashInOutTransaction {
  id?: number;
  type: CashInOutTransaction.Type;
  description: string;
  amount: number;
  branchCurrentAccount: BranchCurrentAccount;
  transactionDate?: string;
}

export namespace CashInOutTransaction {
  export enum Type {
    Cash_In = 'Cash_In',
    Cash_Out = 'Cash_Out'
  }
} 