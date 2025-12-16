package org.example.clean4u.order.orderItemOption;

import lombok.Data;

public class OrderItemOptionResponse {
    @Data
    public static class DetailDto {
        private String optionName;
        private Integer extraPrice;

        public DetailDto(OrderItemOption orderItemOption) {
            if (orderItemOption.getLaundryOption() != null) {
                this.optionName = orderItemOption.getLaundryOption().getName();
                this.extraPrice = orderItemOption.getLaundryOption().getExtraPrice();
            }
        }
    }
}
