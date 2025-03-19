import { MainCategory } from './main-category.model';

export interface SubCategory {
  id: number;
  category: string;
  mainCategory: MainCategory;
} 