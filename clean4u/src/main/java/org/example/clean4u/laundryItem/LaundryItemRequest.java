package org.example.clean4u.laundryItem;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class LaundryItemRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "세탁물 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "카테고리는 필수입니다.")
        private LaundryCategory category;

        @NotBlank(message = "기본 요금은 필수입니다.")
        private Integer basePrice;

        public LaundryItem toEntity() {
            return LaundryItem.builder()
                    .name(name)
                    .category(category)
                    .basePrice(basePrice)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "세탁물 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "카테고리는 필수입니다.")
        private LaundryCategory category;

        @NotBlank(message = "기본 요금은 필수입니다.")
        private Integer basePrice;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("세탁물 이름은 필수입니다.");
            }

            if (category == null) {
                throw new IllegalArgumentException("카테고리는 필수입니다.");
            }

            if (basePrice == null || basePrice < 0) {
                throw new IllegalArgumentException("기본 요금은 0 이상 이어야 합니다.");
            }
        }
    }
}
