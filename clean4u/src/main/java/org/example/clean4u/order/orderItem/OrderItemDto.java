package org.example.clean4u.order.orderItem;

import java.util.List;

public interface OrderItemDto {
    Long getLaundryItemId();
    Integer getQuantity();
    List<Long> getOptionIds();
}
