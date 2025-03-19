package com.prj.LoneHPManagement.model.entity;

import lombok.Getter;
import lombok.Setter;
@Getter
public enum ConstraintEnum {
    PAID(19, "paid"),
    NOT_PAID(20, "not_paid"),
    ACTIVE(13, "active"),
    ALLOWED(16, "allow"),
    LIMITED(17, "limited"),
    IS_FREEZE(1, "is_freeze"),
    NOT_FREEZE(2, "not_freeze"),
    UNDER_REVIEW(3, "under_review"),
    REJECTED(4, "rejected"),
    APPROVED(5, "approved"),
    REPAY_ACTIVE(6, "repay_active"),
    PAST_DUE(7, "past_due"),
    PAID_OFF(9, "paid_off"),
    CLOSED(10, "closed"),
    UNDER_SCHEDULE(11, "under_schedule"),
    GRACE_PERIOD(12, "grace_period"),
    DELETED(21, "deleted"),
    TERMINATED(14, "terminated"),
    RETIRED(15, "retired"),

    DEFAULTED(8, "defaulted"),//90-180 days
    LONG_TERM_OVERDUE(18,"long_term_overdue");


    private final int code;
    private final String description;

    ConstraintEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    public static ConstraintEnum fromCode(int code) {
        for (ConstraintEnum c : ConstraintEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }
}
