import { User } from "./user.model";

export interface Permission {
  id: number;
  name: string;
}

export interface UserPermission {
  id: {
    userId: number;
    permissionId: number;
  };
  permission: Permission;
  isAllowed: string;
  allowedDate: string;
  allowedUser: User;
  limitedDate: string;
} 