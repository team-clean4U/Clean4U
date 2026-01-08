package org.example.clean4u.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PAID("결제완료"),
    REFUND("환불완료");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
}
