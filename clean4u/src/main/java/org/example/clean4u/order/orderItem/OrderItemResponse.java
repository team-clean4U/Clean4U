package org.example.clean4u.order.orderItem;

import lombok.Data;
import org.example.clean4u.laundryItem.LaundryCategory;
import org.example.clean4u.laundryOption.LaundryOption;
import org.example.clean4u.order.orderItemOption.OrderItemOptionResponse;

import java.util.List;

public class OrderItemResponse {
    @Data
    public static class DetailDto {

        private String laundryItemName;
        private LaundryCategory laundryCategory;
        private Integer quantity;

        private Integer basePrice; // 1개당 가격 * quantity
        private Integer optionalTotalPrice;
        private Integer itemTotalPrice; // (기본) * quantity + 옵션

        private List<OrderItemOptionResponse.DetailDto> options;

        public DetailDto(OrderItem orderItem, List<OrderItemOptionResponse.DetailDto> options) {
            this.laundryItemName = orderItem.getLaundryItem().getName();
            this.laundryCategory = orderItem.getLaundryItem().getCategory();
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

    @Data
    public static class UpdateFormDto {
        private Integer index;
        private Long laundryItemId;
        private String laundryItemName;
        private Integer quantity;

        private List<UpdateFormOptionDto> options;

        public UpdateFormDto(Integer index, OrderItem orderItem, List<UpdateFormOptionDto> options) {
            this.index = index;
            if(orderItem.getLaundryItem() != null) {
                this.laundryItemId = orderItem.getLaundryItem().getId();
                this.laundryItemName = orderItem.getLaundryItem().getName();
            }
            this.quantity = orderItem.getQuantity();
            this.options = options;
        }
    }

    @Data
    public static class UpdateFormOptionDto {
        private Long id;
        private String name;
        private Integer extraPrice;
        private boolean checked;

        public UpdateFormOptionDto(LaundryOption laundryOption, boolean checked) {
            this.id = laundryOption.getId();
            this.name = laundryOption.getName();
            this.extraPrice = laundryOption.getExtraPrice();
            this.checked = checked;
        }
    }
}
