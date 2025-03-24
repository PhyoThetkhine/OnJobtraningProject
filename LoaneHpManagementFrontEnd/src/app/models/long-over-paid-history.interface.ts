import { SMELoan } from "./sme-loan.model";

export interface LongOverPaidHistory {
    id: number;
    paidAmount: number;
    paidDate: string; // or Date
    outstandingAmount: number;
    lateFeeAmount: number;
    lateDays: number;
    loan: SMELoan;
}