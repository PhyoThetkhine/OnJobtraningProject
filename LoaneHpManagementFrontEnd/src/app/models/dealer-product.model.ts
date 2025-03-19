import { SubCategory } from './sub-category.model';
import { Company } from './company.model';
import { User } from './user.model';

export interface DealerProduct {
  id: number;
  name: string;
  price: number;
  description: string;
  subCategory: SubCategory;
  company: Company;
  createdUser: User;
  createdDate: string;
  updatedDate: string;
} 