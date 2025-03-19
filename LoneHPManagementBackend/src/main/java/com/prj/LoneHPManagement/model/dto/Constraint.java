package com.prj.LoneHPManagement.model.dto;

public class Constraint {
    public static final int IS_FREEZE = 1;
    public static final int NOT_FREEZE = 2;
    public static final int UNDER_REVIEW = 3;
    public static final int REJECTED = 4;
    public static final int APPROVED = 5;
    public static final int REPAY_ACTIVE = 6;
    public static final int PAST_DUE = 7;
    public static final int DEFAULTED = 8; //The borrower has failed to repay the loan as agreed.
    public static final int PAID_OFF = 9;
    public static final int CLOSED = 10;
    public static final int UNDER_SCHEDULE = 11;
    public static final int GRACE_PERIOD = 12;
    public static final int ACTIVE = 13;
    public static final int TERMINATED = 14;
    public static final int RETIRED = 15;
    public static final int IS_ALLOW = 16;
    public static final int LIMITED = 17;

}
