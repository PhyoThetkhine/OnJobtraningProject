import { HpLoan } from "./hp-loan.model";

export interface HpLongOverPaidHistory {
    id: number;
    paidAmount: number;
    paidDate: string; // or Date
    outstandingAmount: number;
    lateFeeAmount: number;
    lateDays: number;
    loan: HpLoan;
}