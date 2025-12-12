package org.example.clean4u.supplyItem;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class SupplyItemRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "자재 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @NotBlank(message = "재고 수량은 필수입니다.")
        private Integer stockQuantity;

        public SupplyItem toEntity() {
            return SupplyItem.builder()
                    .name(name)
                    .unit(unit)
                    .stockQuantity(stockQuantity)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "자재 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @NotBlank(message = "재고 수량은 필수입니다.")
        private Integer stockQuantity;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("자재 이름은 필수입니다.");
            }

            if (unit == null) {
                throw new IllegalArgumentException("단위는 필수입니다.");
            }

            if (stockQuantity == null || stockQuantity < 0) {
                throw new IllegalArgumentException("재고 수량은 0 이상 이어야 합니다.");
            }
        }
    }
}
