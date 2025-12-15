package org.example.clean4u.order.orderItemOption;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.order.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public class OrderResponse {
    @Data
    @NoArgsConstructor
    public static class DetailDto {
        private Long orderId;
        private String customerName;
        private LocalDate orderDate;
        private OrderStatus status;
        private String memo;
        private Employee editor;

        private Integer totalPrice;
        private List<OrderItemDto> items;

        @Builder
        public DetailDto(Long orderId, String customerName, LocalDate orderDate, OrderStatus status, String memo, Employee editor, Integer totalPrice, List<OrderItemDto> items) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.orderDate = orderDate;
            this.status = status;
            this.memo = memo;
            this.editor = editor;
            this.totalPrice = totalPrice;
            this.items = items;
        }
    }

    @Data
    @NoArgsConstructor
    public static class OrderItemDto {
        private String laundryItemName;
        private Integer quantity;

        private Integer basePrice; // 1개당 가격 * quantity
        private Integer optionalTotalPrice;
        private Integer itemTotalPrice; // (기본) * quantity + 옵션

        private List<OptionDto> options;

        @Builder
        public OrderItemDto(String laundryItemName, Integer quantity, Integer basePrice, Integer optionalTotalPrice, Integer itemTotalPrice, List<OptionDto> options) {
            this.laundryItemName = laundryItemName;
            this.basePrice = basePrice;
            this.quantity = quantity;
            this.optionalTotalPrice = optionalTotalPrice;
            this.itemTotalPrice = itemTotalPrice;
            this.options = options;
        }
    }

    @Data
    @NoArgsConstructor
    public static class OptionDto {
        private String optionName;
        private Integer extraPrice;

        @Builder
        public OptionDto(String optionName, Integer extraPrice) {
            this.optionName = optionName;
            this.extraPrice = extraPrice;
        }
    }
}
