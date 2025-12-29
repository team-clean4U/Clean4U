package org.example.clean4u.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends OrderRepositoryCustom, JpaRepository<Order, Long> {
    Long countByCustomerIdAndStatus(Long customerId, OrderStatus status);

    List<Order> findByCustomerIdOrderByOrderDateAsc(Long customer);

    @Query(value =
            "SELECT COUNT(*) FROM order_tb o " +
            "WHERE o.status = :status"
    , nativeQuery = true)
    Integer countByStatus(@Param("status") String status);

    @Query(value =
            "SELECT COUNT(*) FROM order_tb o " +
            "WHERE o.status = :status " +
                "AND o.updated_at >= :start " +
                "AND o.updated_at < :end"
    , nativeQuery = true)
    Integer countByStatusAndDate(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value =
            "SELECT COUNT(*) FROM order_tb o " +
            "WHERE o.status = 'RECEIVED' " +
                "AND o.order_date = CURDATE()"
    , nativeQuery = true)
    Integer countTodayOrders();

    @Query(value =
            "SELECT SUM(o.total_price) FROM order_tb o " +
            "WHERE o.status = 'RECEIVED' " +
                "AND o.order_date = CURDATE()"
    , nativeQuery = true
    )
    Integer countPriceTodayOrders();
}
