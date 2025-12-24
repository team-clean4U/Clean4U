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

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.일반직원;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.승인대기;

    @Builder
    public Employee(String name, String username, String password, String email, UserRole userRole, UserStatus userStatus) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = userRole != null ? userRole : UserRole.일반직원;
        this.userStatus = userStatus != null ? userStatus : UserStatus.승인대기;
    }

    public void update(EmployeeRequest.UpdateDTD req) {
        this.password = req.getPassword();
        this.email = req.getEmail();
    }

    public boolean isOwner(Long employeeId) {
        return this.id.equals(employeeId);
    }

    public boolean isAdmin() { return this.userRole == UserRole.관리자;}
}
