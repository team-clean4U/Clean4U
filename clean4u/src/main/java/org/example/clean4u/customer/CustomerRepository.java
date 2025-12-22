package org.example.clean4u.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where c.name LIKE concat('%', :keyword, '%') ")
    List<Customer> findByNameContaining(@Param("keyword") String name);

    List<Customer> findByPhoneContaining(@Param("keyword")String keyword);

    @Query("""
    SELECT c FROM Customer c 
    WHERE c.name LIKE CONCAT('%', :keyword, '%') 
        OR c.phone LIKE CONCAT('%', :keyword, '%')
    ORDER BY c.createdAt DESC 
    """)
    List<Customer> searchByKeyword(@Param("keyword") String keyword);
}
