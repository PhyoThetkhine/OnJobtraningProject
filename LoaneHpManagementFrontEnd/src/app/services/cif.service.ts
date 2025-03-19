import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CIF, CIFType } from '../models/cif.model';
import { ApiResponse, PagedResponse } from '../models/common.types';
import { map } from 'rxjs/operators';

// Add Gender enum to match backend
export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE'
}

// Update DTOs to match backend
export interface AddressDTO {
  state: string;
  township: string;
  city: string;
  additionalAddress?: string;
}

export interface AccountDTO {
  minAmount: number;
  maxAmount: number;
}

export interface FinancialInfoDTO {
  averageIncome: number;
  expectedIncome: number;
  averageExpenses: number;
  averageInvestment: number;
  averageEmployees: number;
  averageSalaryPaid: number;
  revenueProof: string;
}

export interface BusinessRegistrationDTO {
  name: string;
  companyType: string;
  category: string;
  businessType: string;
  registrationDate: Date;
  licenseNumber: string;
  licenseIssueDate: Date;
  licenseExpiryDate: Date;
  phoneNumber: string;
  address: AddressDTO;
  businessPhotos: string[];
  financial: FinancialInfoDTO;
  createdUserId: number;
}

export interface ClientRegistrationDTO {
  name: string;
  email: string;
  phoneNumber: string;
  nrc: string;
  photo: string;
  nrcFrontPhoto: string;
  nrcBackPhoto: string;
  dateOfBirth: Date;
  gender: Gender;
  cifType: CIFType;
  address: AddressDTO;
  status: number;
  account: AccountDTO;
  business?: BusinessRegistrationDTO;
  createdUserId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CIFService {
  private apiUrl = `${environment.apiUrl}/cif`;

  constructor(private http: HttpClient) {}

  private formatDateForJava(date: Date | string): Date {
    if (typeof date === 'string') {
      return new Date(date);
    }
    return date;
  }

  createCIF(data: ClientRegistrationDTO): Observable<CIF> {
    // Convert dates to the format expected by Java
    const formattedData = {
      ...data,
      dateOfBirth: this.formatDateForJava(data.dateOfBirth),
      business: data.business ? {
        ...data.business,
        registrationDate: this.formatDateForJava(data.business.registrationDate),
        licenseIssueDate: this.formatDateForJava(data.business.licenseIssueDate),
        licenseExpiryDate: this.formatDateForJava(data.business.licenseExpiryDate)
      } : undefined
    };

    return this.http.post<ApiResponse<CIF>>(`${this.apiUrl}/save`, formattedData)
      .pipe(
        map(response => response.data)
      );
  }

  getCIFs(page: number = 0, size: number = 15, sortBy: string = 'id'): Observable<PagedResponse<CIF>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    
    return this.http.get<ApiResponse<PagedResponse<CIF>>>(`${this.apiUrl}/allCIF`, { params })
      .pipe(
        map(response => response.data)
      );
  }
  getCIFsToSellect(currentUserId: number, searchTerm: string = ''): Observable<CIF[]> {
    const params = { searchTerm };
    return this.http.get<ApiResponse<CIF[]>>(`${this.apiUrl}/allCIFToSelect/${currentUserId}`, { params })
      .pipe(
        map(response => response.data)
      );
  }

  getCIFById(id: number): Observable<CIF> {
    return this.http.get<ApiResponse<CIF>>(`${this.apiUrl}/getBy/${id}`)
      .pipe(
        map(response => response.data)
      );
  }

  

  updateCIF(id: number, cif: Partial<CIF>): Observable<CIF> {
    return this.http.put<ApiResponse<CIF>>(`${this.apiUrl}/update/${id}`, cif)
      .pipe(
        map(response => response.data)
      );
  }

  deleteCIF(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => response.data)
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

  changeClientStatus(id: number, status: 'active' | 'terminated' | 'retired'): Observable<CIF> {
    return this.http.put<ApiResponse<CIF>>(`${this.apiUrl}/status/${id}`, { status })
      .pipe(
        map(response => response.data)
      );
  }
} 