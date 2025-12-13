package org.example.clean4u.order.orderItem;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.order.Order;

import java.util.List;

public class OrderItemRequest {
    @Data
    public static class SaveDto implements OrderItemDto {
        @NotNull(message = "세탁 품목 입력은 필수입니다.")
        private Long laundryItemId;

        @NotNull(message = "수량 입력은 필수입니다.")
        private Integer quantity;

        private List<Long> optionIds; // 세탁 옵션(선택 사항)

        public OrderItem toEntity(Order order, LaundryItem laundryItem) {
            return OrderItem.builder()
                    .order(order)
                    .laundryItem(laundryItem)
                    .quantity(quantity)
                    .build();
        }
    }

    @Data
    public static class UpdateDto implements OrderItemDto {
        @NotNull(message = "세탁 품목 입력은 필수입니다.")
        private Long laundryItemId;

        @NotNull(message = "수량 입력은 필수입니다.")
        private Integer quantity;

        private List<Long> optionIds;

        public OrderItem toEntity(Order order, LaundryItem laundryItem) {
            return OrderItem.builder()
                    .order(order)
                    .laundryItem(laundryItem)
                    .quantity(quantity)
                    .build();
        }
    }
}
