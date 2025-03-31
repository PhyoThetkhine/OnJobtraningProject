import { SubCategory } from './sub-category.model';
import { Company } from './company.model';
import { User } from './user.model';

export interface DealerProduct {
  id: number;
  name: string;
  price: number;
  description: string;
  subCategory: any; // Replace with proper SubCategory type
  cif: any; // Replace with proper CIF type
  createdUser: any; // Replace with proper User type
  createdDate: string | null;
  updatedDate: string | null;
} 