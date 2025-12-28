package org.example.clean4u.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    @Query(value =
            "SELECT AVG(TIMESTAMPDIFF(MINUTE, o.created_at, s.created_at)) " +
            "FROM order_status_history o " +
            "JOIN order_status_history s " +
                "ON o.order_id = s.order_id " +
            "WHERE o.status = 'RECEIVED' " +
                "AND s.status = 'COMPLETED'"
    , nativeQuery = true)
    Double findAverageProcessingMinutes();
}
