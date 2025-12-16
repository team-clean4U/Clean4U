package org.example.clean4u.order;

import org.example.clean4u.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends OrderRepositoryCustom, JpaRepository<Order, Long> {
    Long countByCustomerAndStatus(Customer customer, OrderStatus status);
}
