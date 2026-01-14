package org.example.clean4u.orderStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderStatusHistoryRepository extends OrderStatusHistoryRepositoryCustom, JpaRepository<OrderStatusHistory, Long> {

    @Query(value =
            "SELECT AVG(TIMESTAMPDIFF(MINUTE, o.created_at, s.created_at)) " +
            "FROM order_status_history_tb o " +
            "JOIN order_status_history_tb s " +
                "ON o.order_id = s.order_id " +
            "WHERE o.status = 'RECEIVED' " +
                "AND s.status = 'COMPLETED'"
    , nativeQuery = true)
    Double findAverageProcessingMinutes();

    @Query("SELECT h FROM OrderStatusHistory h JOIN FETCH h.order o JOIN FETCH h.editor e WHERE o.id = :orderId")
    List<OrderStatusHistory> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT sh FROM OrderStatusHistory sh JOIN FETCH sh.editor e JOIN FETCH sh.order o WHERE sh.order.id = :orderId ORDER BY sh.createdAt ASC")
    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(@Param("orderId") Long orderId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM OrderStatusHistory sh WHERE sh.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}
