import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { User, CreateUserDto } from '../models/user.model';
import { ApiResponse, PagedResponse } from '../models/common.types';
import { environment } from '../../environments/environment';
import { UserPermission } from '../models/permission.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/user`;

  constructor(private http: HttpClient) {}

  getUsers(page: number = 0, size: number = 15, sortBy: string = 'id'): Observable<PagedResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    
    return this.http.get<ApiResponse<PagedResponse<User>>>(`${this.apiUrl}/allUsers`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<ApiResponse<User>>(`${this.apiUrl}/findUser/${id}`)
      .pipe(
        map(response => response.data)
      );
  }

  createUser(userData: CreateUserDto): Observable<any> {
    return this.http.post(`${this.apiUrl}/saveUser`, userData);
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/update/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  changeUserStatus(userId: number, status: string): Observable<User> {
    return this.http.put<ApiResponse<User>>(`${this.apiUrl}/changeStatus/${userId}`, { status })
      .pipe(
        map(response => response.data)
      );
  }

  updateUserRole(userId: number, roleId: number): Observable<User> {
    return this.http.put<ApiResponse<User>>(`${this.apiUrl}/changeRole/${userId}`, { roleId })
      .pipe(
        map(response => response.data)
      );
  }

  getUserPermissions(userId: number): Observable<ApiResponse<UserPermission[]>> {
    return this.http.get<ApiResponse<UserPermission[]>>(`${this.apiUrl}/permissions/${userId}`);
  }

  updateUserPermission(userId: number, permissionId: number, status: string): Observable<ApiResponse<UserPermission>> {
    return this.http.put<ApiResponse<UserPermission>>(
      `${this.apiUrl}/permissions/${userId}/${permissionId}`,
      { status }
    );
  }

  getAllUniqueEmails(): Observable<Set<string>> {
    return this.http.get<string[]>(`${this.apiUrl}/emails`).pipe(
      map(emails => new Set(emails))
    );
  }

  getAllUniquePhoneNumbers(): Observable<Set<string>> {
    return this.http.get<string[]>(`${this.apiUrl}/phoneNumbers`).pipe(
      map(phones => new Set(phones))
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = error.error?.message || `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 