import { SMETerm } from "./sme-loan.model";

export interface SMELoanHistory {
    id: number;
    paidAmount: number;
    paidDate: string; // or Date if you'll convert it
    outstanding: number;
    principalPaid: number;
    interestPaid: number;
    iodPaid: number;
    interestLateFeePaid: number;
    principalLateFeePaid: number;
    totalPaid: number;
    smeTerm: { id: number };
    interestLateDays:number;
    principalLateDays:number;
    termNumber?: number; 
    
}