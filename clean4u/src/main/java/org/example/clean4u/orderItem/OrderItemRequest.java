package org.example.clean4u.orderItem;

import lombok.Data;
import org.example.clean4u.laundryItem.LaundryItem;

public class OrderItemRequest {
    @Data
    public static class UpdateDto {
        private LaundryItem laundryItem;
        private Integer quantity;

        public void validate() {
            if (laundryItem == null) {
                throw new IllegalArgumentException("세탁 품목 입력은 필수입니다.");
            }
            if(quantity == null) {
                throw new IllegalArgumentException("수량 입력은 필수입니다.");
            }
        }
    }
}
