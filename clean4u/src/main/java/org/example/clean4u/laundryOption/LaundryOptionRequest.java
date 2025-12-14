package org.example.clean4u.laundryOption;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class LaundryOptionRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "옵션 이름은 필수입니다.")
        private String name;

        @NotNull(message = "추가 요금은 필수입니다.")
        @Min(value = 0, message = "추가 요금은 0 이상 이어야 합니다.")
        private Integer extraPrice;

        private String description;

        public LaundryOption toEntity() {
            return LaundryOption.builder()
                    .name(name)
                    .extraPrice(extraPrice)
                    .description(description)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "옵션 이름은 필수입니다.")
        private String name;

        @NotNull(message = "추가 요금은 필수입니다.")
        @Min(value = 0, message = "추가 요금은 0 이상 이어야 합니다.")
        private Integer extraPrice;

        private String description;
    }
}