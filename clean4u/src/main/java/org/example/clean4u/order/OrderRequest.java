package org.example.clean4u.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.customer.Customer;

import java.time.LocalDate;

public class OrderRequest {
    @Data
    public static class SaveDto {
        @NotNull(message = "고객 id를 입력하세요")
        Long customerId;

        @NotNull(message = "주문 상태를 입력하세요")
        OrderStatus status;

        @NotNull(message = "총 금액을 입력하세요")
        Long totalPrice;

        @NotNull(message = "주문 날짜를 입력하세요")
        LocalDate orderDate;

        String memo;

        public Order toEntity(Customer customer) {
            return Order.builder()
                    .customer(customer)
                    .status(this.status)
                    .totalPrice(this.totalPrice)
                    .orderDate(this.orderDate)
                    .memo(this.memo)
                    .build();
        }
    }

    @Data
    public static class UpdateDto {
        @NotNull(message = "주문 상태를 입력하세요")
        OrderStatus status;

        @NotNull(message = "총 금액을 입력하세요")
        Long totalPrice;

        @NotNull(message = "주문 날짜를 입력하세요")
        LocalDate orderDate;

        String memo;
    }
}
