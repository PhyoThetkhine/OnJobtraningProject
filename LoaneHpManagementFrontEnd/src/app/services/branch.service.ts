import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Branch, CreateBranchDto, BranchStatus } from '../models/branch.model';
import { User } from '../models/user.model';
import { CIF } from '../models/cif.model';
import { environment } from '../../environments/environment';
import { ApiResponse, PagedResponse, ApiPagedResponse } from '../models/common.types';
import { BranchCurrentAccount } from '../models/branch-account.model';
import { BranchDTO } from '../models/branch.dto';

@Injectable({
  providedIn: 'root'
})
export class BranchService {
  private apiUrl = `${environment.apiUrl}/branch`;

  createBranch(branchData: CreateBranchDto): Observable<ApiResponse<Branch>> {
    return this.http.post<ApiResponse<Branch>>(`${this.apiUrl}/create`, branchData);
  }

  constructor(private http: HttpClient) {}

  getBranches(page: number = 0, size: number = 5): Observable<ApiPagedResponse<Branch>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApiPagedResponse<Branch>>(`${this.apiUrl}/branches`, { params });
  }

  getAllBranches(): Observable<Branch[]> {
    return this.http.get<Branch[]>(`${this.apiUrl}/allBranches`);
  }
  getBranchAccount(branchId: number): Observable<ApiResponse<BranchCurrentAccount>> {
    return this.http.get<ApiResponse<BranchCurrentAccount>>(`${environment.apiUrl}/branchAccounts/branch/${branchId}`);
  }
  getBranchById(id: number): Observable<Branch> {
    return this.http.get<Branch>(`${this.apiUrl}/${id}`);
  }

  updateBranch(branch: Branch): Observable<Branch> {
    return this.http.put<Branch>(`${this.apiUrl}/update/${branch.id}`, branch);
  }

  deleteBranch(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getBranchUsers(branchId: number, page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    
    return this.http.get<ApiResponse<any>>(`${environment.apiUrl}/user/${branchId}/users`, { params });
  }

  getBranchClients(branchCode: string, page: number = 0, size: number = 10, sortBy: string = 'id'): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    
    return this.http.get<ApiResponse<any>>(`${environment.apiUrl}/cif/branch/${branchCode}`, { params });
  }

 

  getBranchAccountById(accountId: number): Observable<ApiResponse<BranchCurrentAccount>> {
    return this.http.get<ApiResponse<BranchCurrentAccount>>(`${environment.apiUrl}/branchAccounts/account/${accountId}`);
  }

  changeBranchStatus(branchId: number, status: BranchStatus): Observable<Branch> {
    return this.http.put<Branch>(`${this.apiUrl}/${branchId}/status`, { status });
  }

  getAllActiveBranches(): Observable<BranchDTO[]> {
    return this.http.get<ApiResponse<BranchDTO[]>>(`${this.apiUrl}/active`).pipe(
      map(response => response.data) // Extract the 'data' field
    );
  }
} 