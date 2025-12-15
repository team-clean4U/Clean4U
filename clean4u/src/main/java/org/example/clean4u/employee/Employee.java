package org.example.clean4u.employee;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "employee_tb")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.EMPLOYEE;

    @Builder
    public Employee(String name, String username, String password, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = UserRole.EMPLOYEE;
    }

    public void update(EmployeeRequest.UpdateDTD req) {
        req.validate();

        this.password = req.getPassword();
        this.email = req.getEmail();
    }
}
