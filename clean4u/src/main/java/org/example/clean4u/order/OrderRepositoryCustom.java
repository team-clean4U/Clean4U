package org.example.clean4u.order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepositoryCustom {
     List<Order> searchOrder(
            OrderStatus status,
            String customerName,
            String phone,
            LocalDate fromDate,
            LocalDate toDate
    );
}