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

    Optional<Employee>findByUsername(String username);

    @Query("SELECT e FROM Employee e WHERE e.userStatus = '승인대기' ORDER BY e.createdAt")
    List<Employee> findAllByOrderByCreatedAtDesc();

    Long countByUserStatus(UserStatus status);

    @Query("SELECT e FROM Employee e " +
            "ORDER BY e.createdAt DESC")
    Page<Employee> findAllEmployee(Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.name LIKE concat('%', :keyword, '%') " +
            "ORDER BY e.name DESC")
    Page<Employee> findByNameContaining(@Param("keyword") String name, Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.name LIKE CONCAT('%', :keyword, '%') " +
            "OR e.email LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY e.createdAt DESC")
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE e.email LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY e.email DESC")
    Page<Employee> findByEmailContaining(String email, Pageable pageable);
}
