
export interface ApiResponse<T> {
  success: boolean;
  statusCode: number;
  message: string;
  data: T;
}

export interface BranchDTO {
  id?: number;
  branchCode?: string;
  branchName: string;
  createdUserId: number | null;
  status?: string;
  township: string;
  city: string;
  state: string;
  additionalAddress?: string;
  userCode?: string;
  createdDate?: string;
  updatedDate?: string;

  }
  
  export enum BranchStatus {
    CLOSED = 10,
    ACTIVE = 13,
    TERMINATED = 14
}

