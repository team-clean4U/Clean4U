package org.example.clean4u.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee>findByUsername(String username);
    Optional<Employee>findByUsernameAndPassword(String username, String password);

    List<Employee> findByNameContaining(String name);

    List<Employee> findByEmail(String email);
}
