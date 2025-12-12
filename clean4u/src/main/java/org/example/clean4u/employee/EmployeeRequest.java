package org.example.clean4u.employee;

import lombok.Data;

public class EmployeeRequest {
    @Data
    public static class LoginDTO {
        private String username;
        private String password;

        public void validate() {
            if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("사용자 명을 입력하세요");
            if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("비밀번호를 입력하세요");
        }
    } // end of inner class

    @Data
    public static class JoinDTO {
        private String name;
        private String username;
        private String password;
        private String email;

        public void validate() {
            if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("사용자 명을 입력하세요");
            if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("비밀번호를 입력하세요");
            if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("이메일을 입력하세요");
            if (!email.contains("@")) throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다");
        }

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
        private String password;
        private String email;

        public void validate() {
            if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("비밀번호를 입력하세요");
            if (password.length() < 4) throw new IllegalArgumentException("비밀번호는 4글자 이상이여야 합니다");
            if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("이메일을 입력하세요");
            if (!email.contains("@")) throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다");
        }
    }


}
