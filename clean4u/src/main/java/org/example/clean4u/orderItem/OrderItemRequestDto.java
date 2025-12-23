package org.example.clean4u.orderItem;

import java.util.List;

public interface OrderItemRequestDto {
    Long getLaundryItemId();
    Integer getQuantity();
    List<Long> getOptionIds();
}
