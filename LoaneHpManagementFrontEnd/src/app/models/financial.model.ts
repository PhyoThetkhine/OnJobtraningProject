import { Company } from './company.model';
import { User } from './user.model';

export interface Financial {
  id: number;
  averageIncome: string;
  averageEmployees: string;
  averageSalaryPaid: string;
  revenueProof: string;
  averageExpenses: string;
  expectedIncome: string;
  averageInvestment: string;
  company: Company;
  createdDate: string;
  updatedDate: string;
  createdUser: User;
} 