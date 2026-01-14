package org.example.clean4u.employee;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;

public class EmployeeResponse {
    @Data
    public static class SimpleDTO {
        private Long id;
        private String name;
        private String username;

        public SimpleDTO(Employee employee) {
            this.id = employee.getId();
            this.name = employee.getName();
            this.username = employee.getUsername();
        }
    }

    @Data
    public static class ListDTO {
        private Long id;
        private String name;
        private String username;
        private String email;

        public ListDTO(Employee employee) {
            this.id = employee.getId();
            this.name = employee.getName();
            this.username = employee.getUsername();
            this.email = employee.getEmail();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String name;
        private String username;
        private String password;
        private String email;
        private UserRole userRole;
        private UserStatus userStatus;

        public DetailDTO(Employee employee) {
            this.id = employee.getId();
            this.name = employee.getName();
            this.username = employee.getUsername();
            this.password = employee.getPassword();
            this.email = employee.getEmail();
            this.userRole = employee.getUserRole();
            this.userStatus = employee.getUserStatus();
        }
    }

    @Data
    public static class JoinListDTO {
        private Long id;
        private String name;
        private String email;
        private String username;
        private String createdAt;

        public JoinListDTO(Employee employee) {
            this.id = employee.getId();
            this.name = employee.getName();
            this.email = employee.getEmail();
            this.username = employee.getUsername();
            if (employee.getCreatedAt() != null) {
                this.createdAt = DateUtil.birthFormat(employee.getCreatedAt().toLocalDateTime().toLocalDate());
            }
        }
    }

    @Data
    public static class UpdateDTO {
        private Long id;
        private String name;
        private String email;

        public UpdateDTO(Employee employee) {
            this.id = employee.getId();
            this.name = employee.getName();
            this.email = employee.getEmail();
        }
    }
}
