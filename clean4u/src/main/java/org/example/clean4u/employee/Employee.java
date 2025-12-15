package org.example.clean4u.employee;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.time.BaseTimeEntity;

@Entity
@Data
@NoArgsConstructor
@Table(name = "employee_tb")
public class Employee extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "email", nullable = false, length = 50)
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
        this.password = req.getPassword();
        this.email = req.getEmail();
    }
}
