package org.example.clean4u.employee;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCreate {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void adminInit() {
        if (employeeRepository.findByUsername("admin").isPresent()) {
            return;
        }

        String name = "김관리";
        String username = "admin";
        String password = "00000000";
        String email = "admin@naver.com";
        UserRole userRole = UserRole.ADMIN;
        UserStatus userStatus = UserStatus.APPROVED;

        String hashPwd = passwordEncoder.encode(password);

        Employee admin = Employee.builder()
                .name(name)
                .username(username)
                .password(hashPwd)
                .email(email)
                .userRole(userRole)
                .userStatus(userStatus)
                .build();

        employeeRepository.save(admin);
    }

    @PostConstruct
    public void employeeInit() {
        if (employeeRepository.findByUsername("employee").isPresent()) {
            return;
        }

        String name = "나직원";
        String username = "employee";
        String password = "00000000";
        String email = "employee@naver.com";
        UserRole userRole = UserRole.EMPLOYEE;
        UserStatus userStatus = UserStatus.APPROVED;

        String hashPwd = passwordEncoder.encode(password);

        Employee employee = Employee.builder()
                .name(name)
                .username(username)
                .password(hashPwd)
                .email(email)
                .userRole(userRole)
                .userStatus(userStatus)
                .build();

        employeeRepository.save(employee);
    }
}
