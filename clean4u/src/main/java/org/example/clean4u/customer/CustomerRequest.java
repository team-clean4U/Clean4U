package org.example.clean4u.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class CustomerRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotNull(message = "생년월일은 비어있을 수 없습니다.")
        private LocalDate birth;
        @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "연락처 형식이 맞지 않습니다."
        )
        private String phone;
        @Size(max = 1000, message = "1000자 이상 작성할 수 없습니다.")
        private String memo;

        public Customer toEntity() {
            return new Customer(name, birth, phone, memo);
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        private String name;
        @NotNull(message = "생년월일은 비어있을 수 없습니다.")
        private LocalDate birth;
        @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다.")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "연락처 형식이 맞지 않습니다."
        )
        private String phone;
        @Size(max = 1000, message = "1000자 이상 작성할 수 없습니다.")
        private String memo;
    }
    
}
