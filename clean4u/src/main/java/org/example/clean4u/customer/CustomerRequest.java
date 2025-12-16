package org.example.clean4u.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class CustomerRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotBlank(message = "생년월일은 비어있을 수 없습니다.")
        private LocalDate birth;
        @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
        private String phone;
        @Size(message = "메모는 1000자 이상일 수 없습니다.")
        private String memo;

        public Customer toEntity() {
            return new Customer(name, birth, phone, memo);
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotBlank(message = "생년월일은 비어있을 수 없습니다.")
        private LocalDate birth;
        @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
        private String phone;
        @Size(message = "메모는 1000자 이상일 수 없습니다.")
        private String memo;
    }
}
