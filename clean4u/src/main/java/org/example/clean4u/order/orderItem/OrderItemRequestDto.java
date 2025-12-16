package org.example.clean4u.order.orderItem;

import java.util.List;

public interface OrderItemRequestDto {
    Long getLaundryItemId();
    Integer getQuantity();
    List<Long> getOptionIds();
}
