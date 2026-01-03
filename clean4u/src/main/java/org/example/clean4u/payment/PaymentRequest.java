package org.example.clean4u.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.order.Order;

public class PaymentRequest {
    // 결제 준비 요청 DTO
    @Data
    public static class PrepareDTO {
        @NotNull(message = "주문 값은 필수입니다.")
        private Long orderId;

        @NotNull(message = "결제금액 값은 필수입니다.")
        @Min(value = 0, message = "결제 금액은 0 원보다 커야합니다.")
        private Integer amount;
    }

    @Data
    public static class CompleteDTO {
        @NotNull(message = "주문 값은 필수입니다.")
        private Long orderId;

        @NotNull(message = "impUid 값은 필수입니다.")
        private String impUid;

        @NotNull(message = "merchantUid 값은 필수입니다.")
        private String merchantUid;

        public Payment toEntity(Order order) {
            return Payment.builder()
                    .order(order)
                    .impUid(impUid)
                    .merchantUid(merchantUid)
                    .amount(order.getTotalPrice())
                    .paymentStatus(PaymentStatus.PAID)
                    .build();
        }
    }
}
