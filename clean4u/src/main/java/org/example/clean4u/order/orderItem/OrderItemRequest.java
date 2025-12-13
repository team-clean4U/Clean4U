package org.example.clean4u.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.laundryItem.LaundryItem;

public class OrderItemRequest {
    @Data
    public static class saveDto {
        @NotNull(message = "세탁 품목 입력은 필수입니다.")
        private Long laundryItemId;

        @NotNull(message = "수량 입력은 필수입니다.")
        private Integer quantity;

        public OrderItem toEntity(Order order, LaundryItem laundryItem) {
            return OrderItem.builder()
                    .order(order)
                    .laundryItem(laundryItem)
                    .quantity(quantity)
                    .build();
        }
    }

    @Data
    public static class UpdateDto {
        @NotNull(message = "세탁 품목 입력은 필수입니다.")
        private Long laundryItemId;

        @NotNull(message = "수량 입력은 필수입니다.")
        private Integer quantity;
    }
}
