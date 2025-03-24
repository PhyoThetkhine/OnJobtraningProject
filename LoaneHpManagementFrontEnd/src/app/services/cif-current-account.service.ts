import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { CIFCurrentAccount, FreezeStatus } from '../models/cif-current-account.model';
import { ApiResponse } from '../models/common.types';

@Injectable({
  providedIn: 'root'
})
export class CIFCurrentAccountService {
  private apiUrl = `${environment.apiUrl}/cifCurrentAccounts`;

  constructor(private http: HttpClient) {}

  getAccountByCifId(cifId: number): Observable<CIFCurrentAccount> {
    return this.http.get<ApiResponse<CIFCurrentAccount>>(`${this.apiUrl}/getByCifId/${cifId}`)
      .pipe(
        map(response => response.data)
      );
  }

  updateAccountLimits(accountId: number, data: any): Observable<CIFCurrentAccount> {
    return this.http.put<CIFCurrentAccount>(`${this.apiUrl}/${accountId}/limits`, data);
  }

  changeFreezeStatus(accountId: number, status: FreezeStatus): Observable<CIFCurrentAccount> {
    return this.http.put<ApiResponse<CIFCurrentAccount>>(
      `${this.apiUrl}/changeFreezeStatus/${accountId}/${status}`,
      { status }
    ).pipe(
      map(response => response.data)
    );
  }

  getBranchCIFAccounts(branchCode: string, userid: number): Observable<CIFCurrentAccount[]> {
    return this.http.get<ApiResponse<CIFCurrentAccount[]>>(`${this.apiUrl}/getByBranchCode/${branchCode}/${userid}`)
      .pipe(
        map(response => response.data)
      );
  }
} 