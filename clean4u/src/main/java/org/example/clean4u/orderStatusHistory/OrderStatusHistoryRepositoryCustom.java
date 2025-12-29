package org.example.clean4u.orderStatusHistory;

import org.example.clean4u.order.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderStatusHistoryCustom {
    Page<OrderStatusHistory> searchHistories(Pageable pageable, OrderRequest.SearchDTO searchDTO);
}
