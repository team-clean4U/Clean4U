package org.example.clean4u.orderItemOption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {

    @Query("""
        SELECT oio
        FROM OrderItemOption oio
        JOIN FETCH oio.orderItem
        JOIN FETCH oio.laundryOption
        WHERE oio.orderItem.order.id = :orderId
""")
    List<OrderItemOption> findAllByOrderId(@Param("orderId") Long orderId);

    @Query("""
        SELECT oio
        FROM OrderItemOption oio
        JOIN FETCH oio.laundryOption
        JOIN FETCH oio.orderItem
        WHERE oio.orderItem.id = :orderItemId
        
""")
    List<OrderItemOption> findByOrderItemId(@Param("orderItemId") Long orderItemId);

    @Modifying(clearAutomatically = true)
    @Query("""
        DELETE FROM OrderItemOption oio
        WHERE oio.orderItem.order.id = :orderId
""")
    void deleteByOrderId(@Param("orderId") Long orderId);

    void deleteByOrderItemId(Long id);
}
