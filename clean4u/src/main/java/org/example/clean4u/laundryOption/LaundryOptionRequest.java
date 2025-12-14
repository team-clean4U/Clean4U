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

        @NotNull(message = "옵션 활성 여부는 필수입니다.")
        private Boolean isActive;

        public LaundryOption toEntity() {
            return LaundryOption.builder()
                    .name(name)
                    .extraPrice(extraPrice)
                    .description(description)
                    .isActive(isActive)
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

        @NotNull(message = "옵션 활성 여부는 필수입니다.")
        private Boolean isActive;
    }
}