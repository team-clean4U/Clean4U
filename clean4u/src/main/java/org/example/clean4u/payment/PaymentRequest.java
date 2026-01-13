package org.example.clean4u.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class PaymentRequest {
    @Data
    public static class PrepareDTO {
        @NotNull(message = "주문 값은 필수입니다.")
        private Long orderId;

        @NotNull(message = "결제금액 값은 필수입니다.")
        @Min(value = 0, message = "결제 금액은 0 원보다 커야합니다.")
        private Integer amount;
    }

    @Data
    public static class VerifyDTO {
        @NotNull(message = "주문 값은 필수입니다.")
        private Long orderId;

        @NotNull(message = "impUid 값은 필수입니다.")
        private String impUid;

        @NotNull(message = "merchantUid 값은 필수입니다.")
        private String merchantUid;
    }

    @Data
    public static class SearchDTO {
        @Size(max = 20, message = "고객이름 입력은 최대 20자입니다.")
        private String customerName;

        @Size(max = 50, message = "전화번호 입력은 최대 50자까지입니다.")
        private String phone;

        @Size(max = 100, message = "결제 주문 번호는 최대 100자까지입니다.")
        private String merchantUid;

        private PaymentStatus paymentStatus;

        private LocalDate fromDate;
        private LocalDate toDate;
    }
}
