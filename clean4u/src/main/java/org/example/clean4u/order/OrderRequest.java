package org.example.clean4u.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.order.orderItem.OrderItemRequest;

import java.time.LocalDate;
import java.util.List;

public class OrderRequest {
    @Data
    public static class SaveDto {
        @NotNull(message = "고객 id를 입력하세요")
        private Long customerId;

        @NotNull(message = "주문 상태를 입력하세요")
        private OrderStatus status;

        @NotNull(message = "주문 날짜를 입력하세요")
        private LocalDate orderDate;

        private String memo;

        @NotNull(message = "주문 품목을 입력하세요")
        private List<OrderItemRequest.SaveDto> items;

        public Order toEntity(Customer customer, Long totalPrice, Employee editor) {
            return Order.builder()
                    .customer(customer)
                    .status(status)
                    .totalPrice(totalPrice)
                    .orderDate(orderDate)
                    .memo(memo)
                    .editor(editor)
                    .build();
        }
    }

    @Data
    public static class UpdateDto {
        @NotNull(message = "주문 상태를 입력하세요")
        private OrderStatus status;

        @NotNull(message = "주문 날짜를 입력하세요")
        private LocalDate orderDate;

        private String memo;

        @NotNull(message = "주문 품목을 입력하세요")
        private List<OrderItemRequest.UpdateDto> items;
    }
}
