import { MainCategory } from './main-category.model';

export interface SubCategory {
name: any;
  id: number;
  category: string;
  mainCategory: MainCategory;
  status: string;
} 