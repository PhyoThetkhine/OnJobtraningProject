import { AUTHORITY, Role } from './role.model';

export interface Address {
  id: number;
  state: string;
  city: string;
  township: string;
  additionalAddress: string;
}

export interface Branch {
  id: number;
  branchName: string;
}

export interface CreatedUser {
  id: number;
  userCode: string;
  name: string;
  photo: string;
}

export enum UserStatus {
  ACTIVE = 'active',
  TERMINATED = 'terminated',
  RETIRED = 'retired'
}

export interface User {
  id: number;
  userCode: string;
  name: string;
  email: string;
  phoneNumber: string;
  nrc: string;
  nrcFrontPhoto: string;
  nrcBackPhoto: string;
  dateOfBirth: Date;
  gender: 'MALE' | 'FEMALE';
  photo?: string;
  status: 'active' | 'terminated' | 'retired';
  role?: Role;
  address: Address;
  branch: Branch;
  createdUser: CreatedUser;
  createdDate: Date;
  updatedDate: Date;
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

export interface CreateUserDto {
  name: string;
  email: string;
  phoneNumber: string;
  nrc: string;
  nrcFrontPhoto: string;
  nrcBackPhoto: string;
  dateOfBirth: string;
  gender: Gender;
  address: {
    state: string;
    township: string;
    city: string;
    additionalAddress?: string;
  };
  branch: {
    id: number;
  };
  role: {
    id: number;
  };
  createdUser: {
    id: number;
  };
  photo: string;
  password: string;
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE'
}
export interface CurrentUser {
  id: number;
  userCode: string;
  name: string;
  email: string;
  branchName: string;
  role: Role;
  branch: Branch;
  roleLevel: string;
}