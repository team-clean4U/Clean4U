package org.example.clean4u.order;

import lombok.Data;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.order.orderItem.OrderItemResponse;

import java.time.LocalDate;
import java.util.List;

public class OrderResponse {

    @Data
    public static class ListDto {
        private Long orderId;
        private String customerName;
        private LocalDate localDate;
        private OrderStatus status;

        public ListDto(Order order) {
            this.orderId = order.getId();
            if(order.getCustomer() != null) {
                this.customerName = order.getCustomer().getName();
            }
            this.localDate = order.getOrderDate();
            this.status = order.getStatus();
        }
    }

    @Data
    public static class DetailDto {
        private Long orderId;
        private String customerName;
        private LocalDate orderDate;
        private OrderStatus status;
        private String memo;
        private Employee editor;

        private Integer totalPrice;
        private List<OrderItemResponse.DetailDto> items;

        public DetailDto(Order order, List<OrderItemResponse.DetailDto> items) {
            this.orderId = order.getId();
            if(order.getCustomer() != null) {
                this.customerName = order.getCustomer().getName();
            }
            this.orderDate = order.getOrderDate();
            this.status = order.getStatus();
            this.memo = order.getMemo();
            this.editor = order.getEditor();
            this.totalPrice = order.getTotalPrice();
            this.items = items;
        }
    }

    @Data
    public static class UpdateFormDto {
        private OrderStatus status;
        private String memo;
        private List<OrderItemResponse.DetailDto> items;

        public UpdateFormDto(Order order, List<OrderItemResponse.DetailDto> items) {
            this.status = order.getStatus();
            this.memo = order.getMemo();
            this.items = items;
        }
    }
}
