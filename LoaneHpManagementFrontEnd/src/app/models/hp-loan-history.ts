export interface HpLoanHistory {
    id: number;
    paidAmount: number;
    paidDate: string; // or Date if you'll convert it
    outstanding: number;
    principalPaid: number;
    interestPaid: number;
    iodPaid: number;
    podPaid:number;
    interestLateFeePaid: number;
    principalLateFeePaid: number;
    totalPaid: number;
    hpTerm: { id: number };
    interestLateDays:number;
    principalLateDays:number;
    termNumber?: number; 
    
}