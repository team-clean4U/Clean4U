package org.example.clean4u.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.orderItem.OrderItemRequest;

import java.time.LocalDate;
import java.util.List;

public class OrderRequest {
    @Data
    public static class SaveDTO {
        @NotNull(message = "고객을 선택해주세요")
        private Long customerId;

        @Size(max = 50, message = "메모작성은 최대 50자입니다.")
        private String memo;

        @NotNull(message = "주문 품목을 입력하세요")
        private List<OrderItemRequest.SaveDto> items;

        public Order toEntity(Customer customer, Integer totalPrice, Employee editor) {
            return Order.builder()
                    .customer(customer)
                    .status(OrderStatus.RECEIVED)
                    .totalPrice(totalPrice)
                    .orderDate(LocalDate.now())
                    .memo(memo)
                    .editor(editor)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotNull(message = "주문 상태를 입력하세요")
        private OrderStatus status;

        @Size(max = 50, message = "메모작성은 최대 50자입니다.")
        private String memo;

        @NotNull(message = "주문 품목을 입력하세요")
        private List<OrderItemRequest.UpdateDto> items;
    }

    @Data
    public static class SearchDTO {
        private OrderStatus status;

        @Size(max = 20, message = "고객이름 입력은 최대 20자입니다.")
        private String customerName;

        @Size(max = 50, message = "전화번호 입력은 최대 50자까지입니다.")
        private String phone;

        private LocalDate fromDate;
        private LocalDate toDate;
    }
}
