package org.example.clean4u.order.orderItem;

import lombok.Data;
import org.example.clean4u.order.orderItemOption.OrderItemOptionResponse;

import java.util.List;

public class OrderItemResponse {
    @Data
    public static class DetailDto {
        private String laundryItemName;
        private Integer quantity;

        private Integer basePrice; // 1개당 가격 * quantity
        private Integer optionalTotalPrice;
        private Integer itemTotalPrice; // (기본) * quantity + 옵션

        private List<OrderItemOptionResponse.DetailDto> options;

        public DetailDto(OrderItem orderItem, List<OrderItemOptionResponse.DetailDto> options) {
            this.laundryItemName = orderItem.getLaundryItem().getName();
            this.quantity = orderItem.getQuantity();
            int base = orderItem.getLaundryItem().getBasePrice();
            this.basePrice = base * orderItem.getQuantity();
            this.optionalTotalPrice = options.stream()
                    .mapToInt(OrderItemOptionResponse.DetailDto::getExtraPrice)
                    .sum();
            this.itemTotalPrice = this.basePrice + this.optionalTotalPrice;
            this.options = options;
        }
    }
}
