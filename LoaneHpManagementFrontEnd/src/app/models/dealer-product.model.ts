import { SubCategory } from './sub-category.model';
import { CIF } from './cif.model';
import { User } from './user.model';

export interface DealerProduct {
  id: number;
  name: string;
  price: number;
  description: string;
  subCategory: SubCategory;
  cif: CIF;
  createdUser: User;
  createdDate: string;
  updatedDate: string;
  status: string;
}