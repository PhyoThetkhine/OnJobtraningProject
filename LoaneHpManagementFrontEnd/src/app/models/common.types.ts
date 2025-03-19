export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE'
}

export enum CIFType {
  DEVELOPED_COMPANY = 'DEVELOPED_COMPANY',
  SETUP_COMPANY = 'SETUP_COMPANY',
  PERSONAL = 'PERSONAL'
}

export enum Status {
  ACTIVE = 13,
  TERMINATED = 14,
  CLOSED = 10
}

export interface Address {
  id: number;
  state: string;
  township: string;
  city: string;
  additionalAddress?: string;
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ApiPagedResponse<T> {
  status: number;
  message: string;
  data: {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
  };
} 