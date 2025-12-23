package org.example.clean4u.orderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.laundryItem
        WHERE oi.order.id = :orderId
""")
    List<OrderItem> findAllByOrderId(@Param("orderId") Long orderId);

    @Modifying(clearAutomatically = true)
    @Query("""
        DELETE FROM OrderItem oi
        WHERE oi.order.id = :orderId
""")
    void deleteByOrderId(@Param("orderId") Long orderId);

    Optional<OrderItem> findByIdAndOrderId(Long orderItemId, Long orderId);
}
