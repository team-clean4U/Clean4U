package org.example.clean4u.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    Optional<Employee>findByUsername(String username);
    Optional<Employee>findByUsernameAndPassword(String username, String password);

    List<Employee> findByNameContaining(String name);

    @Query("SELECT e FROM Employee e WHERE e.userStatus = '승인대기' ORDER BY e.createdAt")
    List<Employee> findAllByOrderByCreatedAtDesc();

    Long countByUserStatus(UserStatus status);

}
