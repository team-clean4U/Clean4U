package org.example.clean4u.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c " +
            "WHERE c.name LIKE concat('%', :keyword, '%')")
    Page<Customer> findByNameContaining(@Param("keyword") String name, Pageable pageable);

    Page<Customer> findByPhoneContaining(@Param("keyword")String keyword, Pageable pageable);

    Page<Customer> findCustomersByGrade(@Param("keyword") Grade grade, Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.name LIKE CONCAT('%', :keyword, '%') " +
            "OR c.phone LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY c.createdAt DESC")
    Page<Customer> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "ORDER BY c.createdAt DESC")
    Page<Customer> findAllCustomers(Pageable pageable);

    @Query(value =
            "SELECT AVG(o.total_price) FROM customer_tb c " +
            "JOIN (SELECT customer_id, SUM(total_price) as total_price FROM order_tb GROUP BY customer_id) o " +
            "ON c.id = o.customer_id " +
            "WHERE c.grade = :grade;"
    , nativeQuery = true)
    Integer findAverageTotalPriceByGrade(@Param("grade") String grade);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    Page<Customer> findAllAndIsActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "WHERE (c.name LIKE CONCAT('%', :keyword, '%') " +
                "OR c.phone LIKE CONCAT('%', :keyword, '%')) " +
                "AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    Page<Customer> searchByKeywordAndIsActiveTrue(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.grade = :keyword " +
                "AND c.isActive = true " +
            "ORDER BY c.createdAt DESC ")
    Page<Customer> findAllByGradeAndIsActiveTrue(@Param("keyword") Grade grade, Pageable pageable);

    boolean existsByPhone(@NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
                          @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "연락처 형식이 맞지 않습니다.")
                          String phone);

    Optional<Customer> findByPhone(String phone);
}
