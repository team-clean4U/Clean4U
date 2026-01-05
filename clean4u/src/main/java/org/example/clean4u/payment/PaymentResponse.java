package org.example.clean4u.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

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
        private Integer amount;

        public VerifyDTO(Integer amount) {
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
}
