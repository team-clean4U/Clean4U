package org.example.clean4u.orderStatusHistory;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u._core.utils.DateUtil;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderStatus;

import java.sql.Timestamp;

public class OrderStatusHistoryResponse {
    @Data
    @NoArgsConstructor
    public static class DetailDTO {
        private Long orderId;
        private OrderStatus status;
        private String editor;
        private String customerName;
        private String phone;
        private String createdAt;

        @Builder
        public DetailDTO(Long orderId, OrderStatus status, String editor, Order order, Timestamp createdAt) {
            this.orderId = orderId;
            this.status = status;
            this.editor = editor;
            if(order != null) {
                this.customerName = order.getCustomer().getName();
                this.phone = order.getCustomer().getPhone();
            }
            this.createdAt = DateUtil.timestampFormat(createdAt);
        }

        public static DetailDTO from(OrderStatusHistory history) {
            return DetailDTO.builder()
                    .orderId(history.getOrder().getId())
                    .status(history.getStatus())
                    .editor(history.getEditor().getName())
                    .order(history.getOrder())
                    .createdAt(history.getCreatedAt())
                    .build();
        }
    }
}
