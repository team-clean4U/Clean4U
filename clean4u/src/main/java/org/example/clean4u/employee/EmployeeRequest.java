package org.example.clean4u.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class EmployeeRequest {
    @Data
    public static class LoginDTO {
        @NotBlank(message = "유저 이름은 비어있을 수 없습니다.")
        @Size(min = 4, max = 20, message = "유저 이름은 4~20자 사이여야 합니다.")
        private String username;
        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        private String password;

    } // end of inner class

    @Data
    public static class JoinDTO {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotBlank(message = "유저 이름은 비어있을 수 없습니다.")
        @Size(min = 4, max = 20, message = "유저 이름은 4~20자 사이여야 합니다.")
        private String username;
        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        private String password;
        @NotBlank(message = "이메일은 비어있을 수 없습니다.")
        private String email;

        // JoinDTO 를 User 타입으로 변환시키는 기능
        public Employee toEntity() {
            return Employee.builder()
                    .name(this.name)
                    .username(this.username)
                    .password(this.password)
                    .email(this.email)
                    .build();
        }
    } // end of inner class

    @Data
    public static class UpdateDTD {
        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        private String password;
        @NotBlank(message = "이메일은 비어있을 수 없습니다.")
        private String email;
    }

    @Data
    public static class EmailCheck {
        @NotBlank(message = "이메일은 비어있을 수 없습니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
        private String code;
    }

}
