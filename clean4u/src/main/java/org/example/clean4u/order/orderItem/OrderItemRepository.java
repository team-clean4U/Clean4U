package org.example.clean4u.order.orderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.laundryItem
        WHERE oi.order.id = :orderId
""")
    List<OrderItem> findAllByOrderId(@Param("orderId") Long orderId);

    void deleteByOrderId(Long orderId);
}
