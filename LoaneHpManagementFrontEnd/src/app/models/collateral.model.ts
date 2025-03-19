import { SMELoan } from './sme-loan.model';
import { User } from './user.model';


export interface Collateral {
  id: number;
  propertyType: string;
  documentUrl: string;
  estimatedValue: number;
  loan: SMELoan;
  createdUser: User;
  createdDate: string;
  updatedDate: string;
}

