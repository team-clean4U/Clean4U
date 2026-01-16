package org.example.clean4u.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByNameContaining(String name);

    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.userStatus = 'PENDING' ORDER BY e.createdAt")
    List<Employee> findAllByOrderByCreatedAtDesc();

    Long countByUserStatus(UserStatus status);

    @Query("SELECT e FROM Employee e " +
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
}
