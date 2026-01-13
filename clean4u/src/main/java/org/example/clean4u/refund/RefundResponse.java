package org.example.clean4u.refund;

import lombok.Data;

public class RefundResponse {
    @Data
    public static class ListDTO {
        private Long paymentId;
        private Integer amount;
        private String reason;
        private String status;
        private boolean isPending;
        private boolean isApproved;
        private boolean isRejected;

        public ListDTO(Refund refund) {
            if(refund.getPayment() != null) {
                this.paymentId = refund.getPayment().getId();
                this.amount = refund.getPayment().getAmount();
            }
            this.reason = refund.getReason();
            this.status = refund.getStatus().getDisplayName();

            this.isPending = refund.getStatus() == RefundStatus.PENDING;
            this.isApproved = refund.getStatus() == RefundStatus.APPROVED;
            this.isRejected = refund.getStatus() == RefundStatus.CANCELLED;
        }
    }

    @Data
    public static class PortOneRefundApprove {
        private Integer code;
        private String message;
    }

    @Data
    public static class RefundConfirmDTO {
        private Boolean isApproved;
    }
}
