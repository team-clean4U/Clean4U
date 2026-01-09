package org.example.clean4u.refund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class RefundRequest {
    @Data
    public static class DetailDTO {
        @NotNull(message = "주문 ID는 필수입니다.")
        private Long orderId;

        @NotNull(message = "결제 ID는 필수입니다.")
        private Long paymentId;

        @NotBlank(message = "환불 사유 입력은 필수입니다.")
        @Size(max = 255, message = "사유 입력은 최대 255자까지 가능합니다.")
        private String reason;
    }
}
