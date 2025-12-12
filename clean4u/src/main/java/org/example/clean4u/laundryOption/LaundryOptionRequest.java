package org.example.clean4u.laundryOption;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class LaundryOptionRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "옵션 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "추가 요금은 필수입니다.")
        private Integer extraPrice;

        public LaundryOption toEntity() {
            return LaundryOption.builder()
                    .name(name)
                    .extraPrice(extraPrice)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "옵션 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "추가 요금은 필수입니다.")
        private Integer extraPrice;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("옵션 이름은 필수입니다.");
            }

            if (extraPrice == null || extraPrice < 0) {
                throw new IllegalArgumentException("추가 요금은 0 이상 이어야 합니다.");
            }
        }
    }
}