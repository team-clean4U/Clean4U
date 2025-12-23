package org.example.clean4u.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends OrderRepositoryCustom, JpaRepository<Order, Long> {
    Long countByCustomerIdAndStatus(Long customerId, OrderStatus status);

    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customer);
}
