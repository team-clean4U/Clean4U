package org.example.clean4u.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;
import org.example.clean4u._core.utils.PriceUtil;

public class PaymentResponse {
    @Data
    public static class PrepareDTO {
        private String merchantUid;
        private Integer amount;

        public PrepareDTO(String merchantUid, Integer amount) {
            this.merchantUid = merchantUid;
            this.amount = amount;
        }
    }

    @Data
    public static class VerifyDTO {
        private Long paymentId;
        private Integer amount;

        public VerifyDTO(Long paymentId, Integer amount) {
            this.paymentId = paymentId;
            this.amount = amount;
        }
    }

    // 포트원 액세스 토큰 응답 DTO
    @Data
    public static class PortOneTokenResponse {
        private int code;
        private String message;
        private ResponseData response;

        @Data
        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class ResponseData {
            private String accessToken;
            private int now;
            private int expiredAt;
        }
    }

    // 포트원 결제 조회 응답 DTO
    @Data
    public static class PortOnePaymentResponse {
        private int code;
        private String message;
        private PaymentData response;

        @Data
        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class PaymentData {
            private Integer amount;
            private String impUid;
            private String merchantUid;
            private String status;
            private Long paidAt;
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private Long orderId;
        private String impUid;
        private String merchantUid;
        private String amount;
        private String paidAt;
        private PaymentStatus status;
        private Boolean isRefundable;

        public DetailDTO(Payment payment, Boolean isRefundable) {
            this.id = payment.getId();
            this.orderId = payment.getOrder().getId();
            this.impUid = payment.getImpUid();
            this.merchantUid = payment.getMerchantUid();
            this.amount = PriceUtil.format(payment.getAmount());
            this.paidAt = DateUtil.timestampFormat(payment.getCreatedAt());
            this.status = payment.getPaymentStatus();
            this.isRefundable = isRefundable != null ? isRefundable : false;
        }

        public DetailDTO(Payment payment) {
            this(payment, payment.getPaymentStatus() == PaymentStatus.PAID);
        }
    }
}
