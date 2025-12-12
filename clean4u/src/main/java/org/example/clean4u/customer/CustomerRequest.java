package org.example.clean4u.customer;

import lombok.Data;

import java.time.LocalDate;

public class CustomerRequest {

    @Data
    public static class saveDto {
        private String name;
        private LocalDate birth;
        private String phone;
        private String memo;

        public Customer toEntity() {
            return new Customer(name, birth, phone, memo);
        }
    }

    @Data
    public static class updateDto {
        private String name;
        private LocalDate birth;
        private String phone;
        private String memo;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }

            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("휴대폰 번호는 필수입니다.");
            }
        }
    }
}
