package org.example.clean4u.order;

import lombok.Data;

import java.time.LocalDate;

public class OrderRequest {
    @Data
    public static class UpdateDto {
        OrderStatus status;
        Long totalPrice;
        LocalDate orderDate;
        String memo;

        public void validate() {
            if(status == null) {
                throw new IllegalArgumentException("주문 상태를 입력하세요");
            }
            if(totalPrice == null) {
                throw new IllegalArgumentException("총 금액을 입력하세요");
            }
        }
    }
}
