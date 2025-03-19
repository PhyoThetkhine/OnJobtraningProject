export interface UserCurrentAccount {
  id: number;
  accCode: string;
  balance: number;
  isFreeze: FreezeStatus;
  createdDate: string;
  updatedDate: string;
  user?: {
    id: number;
    name: string;
  };
}

export enum FreezeStatus {
  IS_FREEZE = 'is_freeze',
  NOT_FREEZE = 'not_freeze'
} 