import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Role } from '../models/role.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';

import { ApiResponse, User } from '../models/user.model';
import { RolePermission } from '../models/role-permission.model';
import { PagedResponse } from '../models/common.types';
import { Permission } from '../models/permission.model';
import { AUTHORITY } from '../models/role.model';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private apiUrl = `${environment.apiUrl}/role`;

  constructor(private http: HttpClient) {}

  getRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.apiUrl}/allRoles`);
  }

  getRegularBranchLevelRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.apiUrl}/regularBranchLevelRole`);
  }

  getRoleById(id: number): Observable<Role> {
    return this.http.get<ApiResponse<Role>>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  getRoleUsers(roleId: number, page: number = 0, size: number = 10): Observable<PagedResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApiResponse<PagedResponse<User>>>(`${this.apiUrl}/${roleId}/users`, { params })
      .pipe(map(response => response.data));
  }

  getRolePermissions(roleId: number): Observable<RolePermission[]> {
    return this.http.get<ApiResponse<RolePermission[]>>(`${this.apiUrl}/${roleId}/permissions`)
      .pipe(map(response => response.data));
  }

  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<ApiResponse<Permission[]>>(`${environment.apiUrl}/permissions/all`)
      .pipe(map(response => response.data));
  }

  createRole(roleData: {
    roleName: string;
    authority: AUTHORITY;
    permissions: number[];
  }): Observable<Role> {
    return this.http.post<ApiResponse<Role>>(`${this.apiUrl}/create`, roleData)
      .pipe(map(response => response.data));
  }

  addRolePermission(roleId: number, permissionId: number): Observable<void> {
    return this.http.post<ApiResponse<void>>(
      `${this.apiUrl}/${roleId}/permissions/${permissionId}`,
      {}
    ).pipe(map(response => response.data));
  }

  removeRolePermission(roleId: number, permissionId: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(
      `${this.apiUrl}/${roleId}/permissions/${permissionId}`
    ).pipe(map(response => response.data));
  }

  updateRole(roleId: number, roleData: {
    roleName: string;
    authority: AUTHORITY;
  }): Observable<Role> {
    return this.http.put<ApiResponse<Role>>(
      `${this.apiUrl}/update/${roleId}`,
      roleData
    ).pipe(map(response => response.data));
  }
} 