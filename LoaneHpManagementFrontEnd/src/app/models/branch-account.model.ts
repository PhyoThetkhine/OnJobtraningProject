import { Branch } from './branch.model';

export interface BranchCurrentAccount {
  id: number;
  accCode: string;
  branch: Branch;
  balance: number;
  createdDate: string;
  updatedDate: string;
} 