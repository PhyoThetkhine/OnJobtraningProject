import { Address } from './address.model';
import { User } from './user.model';

export enum BranchStatus {
  CLOSED = 'closed',
  ACTIVE = 'active',
  TERMINATED = 'terminated'
}

export interface Branch {
  id?: number;
  branchCode?: string;
  branchName: string;
  address: Address;
  createdDate?: string;
  updatedDate?: string;
  createdUser?: User;
  status?: BranchStatus;
  photo?: string;
}

export interface CreateBranchDto {
  branchName: string;
  address: {
    state: string;
    township: string;
    city: string;
    additionalAddress?: string;
  };
  createdUser: {
    id: number;
  };
  status?: BranchStatus;
} 