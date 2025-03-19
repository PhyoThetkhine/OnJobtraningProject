import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, BehaviorSubject } from 'rxjs';
import { CurrentUser, User } from '../models/user.model';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import {jwtDecode} from 'jwt-decode';

interface LoginResponse {
  status: number;
  message: string;
  data: string;
}

interface DecodedToken {
  sub: string;
  permissions: string[];
  exp: number;
  iat: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private userPermissions = new BehaviorSubject<string[]>([]);
  permissions$ = this.userPermissions.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadStoredPermissions();
  }

  private loadStoredPermissions(): void {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: DecodedToken = jwtDecode(token);
        this.userPermissions.next(decoded.permissions || []);
      } catch (error) {
        console.error('Error decoding token:', error);
        this.userPermissions.next([]);
      }
    }
  }

  login(credentials: { userCode: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.data) {
          const token = response.data.split(': ')[1];
          localStorage.setItem('token', token);
          try {
            const decoded: DecodedToken = jwtDecode(token);
            this.userPermissions.next(decoded.permissions || []);
          } catch (error) {
            console.error('Error decoding token:', error);
            this.userPermissions.next([]);
          }
        }
      })
    );
  }
  
  getCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${this.apiUrl}/current-user`);
  }


  logout(): void {
    localStorage.removeItem('token');
    this.userPermissions.next([]);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  hasPermission(permission: string): boolean {
    const currentPermissions = this.userPermissions.value;
    return currentPermissions.includes(permission);
  }

  hasAnyPermission(permissions: string[]): boolean {
    const currentPermissions = this.userPermissions.value;
    return permissions.some(permission => currentPermissions.includes(permission));
  }
}