package org.example.clean4u.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e " +
            "WHERE e.name LIKE concat('%', :keyword, '%') AND e.userStatus = 'APPROVED' " +
            "ORDER BY e.name DESC")
    List<Employee> findByNameContaining(@Param("keyword") String name);

    Optional<Employee> findByUsername(String username);

    @Query("SELECT e FROM Employee e WHERE e.userStatus = 'PENDING' ORDER BY e.createdAt")
    List<Employee> findAllByOrderByCreatedAtDesc();

    @Query("SELECT e FROM Employee e WHERE e.id = :id AND e.userStatus = 'APPROVED'")
    Optional<Employee> findByIdAndApproved(@Param("id") Long id);

    Long countByUserStatus(UserStatus status);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.userStatus = 'APPROVED'" +
            "ORDER BY e.createdAt DESC")
    Page<Employee> findAllEmployee(Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.name LIKE concat('%', :keyword, '%') AND e.userStatus = 'APPROVED' " +
            "ORDER BY e.name DESC")
    Page<Employee> findByNameContaining(@Param("keyword") String name, Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.name LIKE CONCAT('%', :keyword, '%') AND e.userStatus = 'APPROVED' " +
            "OR e.email LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY e.createdAt DESC")
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.email LIKE CONCAT('%', :keyword, '%') AND e.userStatus = 'APPROVED' " +
            "ORDER BY e.email DESC")
    Page<Employee> findByEmailContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Employee e ")
    long countAllEmployees();

    @Query(value =
            "SELECT lo.name, COUNT(oo.laundry_option_id) AS count " +
            "FROM order_item_option_tb oo " +
            "JOIN laundry_option_tb lo ON oo.laundry_option_id = lo.laundry_option_id " +
            "GROUP BY lo.laundry_option_id, lo.name " +
            "ORDER BY count DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5Option();

    @Query(value =
            "SELECT li.category, SUM(li.base_price * oi.quantity) AS total_revenue " +
            "FROM order_item_tb oi " +
            "JOIN laundry_item_tb li ON oi.laundry_item_id = li.laundry_item_id " +
            "GROUP BY li.category " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> findRevenueByCategory();

    @Query(value =
            "SELECT DATE_FORMAT(order_date, '%Y-%m') AS order_month, " +
            "COUNT(id) AS order_count, " +
            "SUM(total_price) AS monthly_revenue " +
            "FROM order_tb " +
            "WHERE status != 'CANCELLED' " +
            "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
            "ORDER BY order_month ASC", nativeQuery = true)
    List<Object[]> findMonthSalesTrend();
}
