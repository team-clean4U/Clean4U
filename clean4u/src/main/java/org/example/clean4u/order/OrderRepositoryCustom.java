package org.example.clean4u.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderRepositoryCustom {
     Page<Order> searchOrder(
             Pageable pageable,
            OrderStatus status,
            String customerName,
            String phone,
            LocalDate fromDate,
            LocalDate toDate
    );
}