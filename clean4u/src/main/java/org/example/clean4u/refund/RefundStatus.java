package org.example.clean4u.refund;

import lombok.Getter;

@Getter
public enum RefundStatus {
    PENDING("환불 대기", "status-received"), // PG 사의 환불 승인 대기 상태 (관리자의 승인 대기 아님)
    APPROVED("환불 완료", "status-completed"),
    REJECTED("환불 취소", "status-cancelled"); // 어떠한 이유로 환불 거절됨

    private final String displayName;
    private final String cssClass;

    RefundStatus(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }
}
