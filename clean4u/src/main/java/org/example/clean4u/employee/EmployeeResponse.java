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
}
