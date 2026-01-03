package org.example.clean4u.payment;

import lombok.Data;

public class PaymentResponse {
    @Data
    public static class PrepareDTO {
        private String merchantUid;
        private Integer amount;
        private String impKey;

        public PrepareDTO(String merchantUid, Integer amount, String impKey) {
            this.merchantUid = merchantUid;
            this.amount = amount;
            this.impKey = impKey;
        }
    }

    @Data
    public static class CompleteDTO {
        private String impUid;
        private String merchantUid;
        private Integer amount;
        private PaymentStatus paymentStatus;
    }
}
