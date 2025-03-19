import { Branch } from './branch.model';
import { Address } from './common.types';
import { User } from './user.model';

export enum CIFType {
  DEVELOPED_COMPANY = 'DEVELOPED_COMPANY',
  SETUP_COMPANY = 'SETUP_COMPANY',
  PERSONAL = 'PERSONAL'
}

// Remove UserCIFBase since we're defining our own properties
export interface CIF {
  id: number;
  cifCode: string;  // This will be auto-generated
  cifType: CIFType;
  name: string;
  email: string;
  phoneNumber: string;
  nrc: string;
  nrcFrontPhoto: string;
  nrcBackPhoto: string;
  dateOfBirth: Date;
  gender: 'MALE' | 'FEMALE';
  //branch: Branch;
  address: Address;
  createdUser: User;
  createdDate: string;
  updatedDate: string;
  photo: string;
  status?: string;
}

// Use Client as an alias for frontend reference
export type Client = CIF; 