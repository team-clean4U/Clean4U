package org.example.clean4u.orderStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    @Query(value =
            "SELECT AVG(TIMESTAMPDIFF(MINUTE, o.created_at, s.created_at)) " +
            "FROM order_status_history_tb o " +
            "JOIN order_status_history_tb s " +
                "ON o.order_id = s.order_id " +
            "WHERE o.status = 'RECEIVED' " +
                "AND s.status = 'COMPLETED'"
    , nativeQuery = true)
    Double findAverageProcessingMinutes();

    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(@Param("orderId") Long orderId);
}
