package org.example.clean4u.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
     Page<Order> searchOrders(Pageable pageable, OrderRequest.SearchDTO searchDTO);
}