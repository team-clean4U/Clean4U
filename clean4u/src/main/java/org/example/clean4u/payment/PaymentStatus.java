package org.example.clean4u.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PAID("결제완료", "status-paid"),
    REFUND("환불완료", "status-refund");

    private final String displayName;
    private final String cssClass;

    PaymentStatus(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }
}
