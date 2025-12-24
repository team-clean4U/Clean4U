package org.example.clean4u.orderItemOption;

import lombok.Data;
import org.example.clean4u._core.utils.PriceUtil;

public class OrderItemOptionResponse {
    @Data
    public static class DetailDto {
        private String optionName;
        private String extraPrice;

        public DetailDto(OrderItemOption orderItemOption) {
            if (orderItemOption.getLaundryOption() != null) {
                this.optionName = orderItemOption.getLaundryOption().getName();
                int extraPriceInt = orderItemOption.getLaundryOption().getExtraPrice();
                this.extraPrice = PriceUtil.format(extraPriceInt);
            }
        }
    }
}
