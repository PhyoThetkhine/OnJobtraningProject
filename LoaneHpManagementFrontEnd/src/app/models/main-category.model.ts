
export enum MainCategoryStatus {
  ACTIVE = 'active',
  DELETED = 'deleted' // Changed from INACTIVE to DELETED to match ConstraintEnum
}

export interface MainCategory {
  id: number;
  category: string;
  status: MainCategoryStatus;
}

export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}