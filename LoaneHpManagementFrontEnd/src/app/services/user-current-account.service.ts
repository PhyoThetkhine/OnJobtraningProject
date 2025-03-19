import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { UserCurrentAccount, FreezeStatus } from '../models/user-current-account.model';
import { ApiResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class UserCurrentAccountService {
  private apiUrl = `${environment.apiUrl}/user-current-account`;

  constructor(private http: HttpClient) {}

  getAccountByUserId(userId: number): Observable<UserCurrentAccount> {
    return this.http.get<ApiResponse<UserCurrentAccount>>(`${this.apiUrl}/getByUserId/${userId}`)
      .pipe(
        map(response => response.data)
      );
  }

  changeFreezeStatus(accountId: number, status: FreezeStatus): Observable<UserCurrentAccount> {
    return this.http.put<ApiResponse<UserCurrentAccount>>(
      `${this.apiUrl}/changeStatus/${accountId}/${status}`,
      {}
    ).pipe(
      map(response => response.data)
    );
  }
} 