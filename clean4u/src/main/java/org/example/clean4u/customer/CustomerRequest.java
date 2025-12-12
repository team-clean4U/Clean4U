package org.example.clean4u.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

public class CustomerRequest {

    @Data
    public static class saveDto {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotBlank(message = "생년월일은 비어있을 수 없습니다.")
        private LocalDate birth;
        @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
        private String phone;
        private String memo;

        public Customer toEntity() {
            return new Customer(name, birth, phone, memo);
        }
    }

    @Data
    public static class updateDto {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotBlank(message = "생년월일은 비어있을 수 없습니다.")
        private LocalDate birth;
        @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
        private String phone;
        private String memo;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }

            if (birth == null) {
                throw new IllegalArgumentException("생년월일은 필수입니다.");
            }

            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("휴대폰 번호는 필수입니다.");
            }
        }
    }
}
