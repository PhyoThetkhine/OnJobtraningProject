import { CIF } from './cif.model';
import { User } from './user.model';
import { Address } from './common.types';

export interface Company {
  id: number;
  name: string;
  companyType: string;
  category: string;
  businessType: string;
  registrationDate: Date;
  licenseNumber: string;
  licenseIssueDate: Date;
  licenseExpiryDate: Date;
  phoneNumber: string;
  address: Address;
  createdUser: User;
  createdDate: string;
  updatedDate: string;
  cif: CIF;
} 