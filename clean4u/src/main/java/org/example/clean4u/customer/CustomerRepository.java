package org.example.clean4u.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c " +
            "WHERE c.name LIKE concat('%', :keyword, '%')")
    Page<Customer> findByNameContaining(@Param("keyword") String name, Pageable pageable);

    Page<Customer> findByPhoneContaining(@Param("keyword")String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.name LIKE CONCAT('%', :keyword, '%') " +
            "OR c.phone LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY c.createdAt DESC")
    Page<Customer> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "ORDER BY c.createdAt DESC")
    Page<Customer> findAllCustomers(Pageable pageable);
}
