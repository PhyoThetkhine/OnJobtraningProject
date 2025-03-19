export enum AUTHORITY {
  MainBranchLevel = 'MainBranchLevel',
  RegularBranchLevel = 'RegularBranchLevel'
}

export interface Role {
  id: number;
  roleName: string;
  createdDate: string;
  updatedDate: string;
  authority: AUTHORITY;
} 