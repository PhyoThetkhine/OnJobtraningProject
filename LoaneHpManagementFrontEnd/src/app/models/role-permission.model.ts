import { Permission } from './permission.model';
import { Role } from './role.model';

export interface RolePermission {
  id: {
    roleId: number;
    permissionId: number;
  };
  role: Role;
  permission: Permission;
} 