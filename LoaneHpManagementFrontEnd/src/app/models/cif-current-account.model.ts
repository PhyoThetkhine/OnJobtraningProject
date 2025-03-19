import { CIF } from './cif.model';

export enum FreezeStatus {
  NOT_FREEZE = 'not_freeze',
  IS_FREEZE = 'is_freeze'
}

export interface CIFCurrentAccount {
  id: number;
  accCode: string;
  balance: number;
  minAmount: number;
  maxAmount: number;
  holdAmount: number;
  isFreeze: FreezeStatus;
  createdDate: string;
  updatedDate: string;
  cif: CIF;
} 