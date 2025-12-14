package org.example.clean4u.supplyItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class SupplyItemRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "자재 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @NotNull(message = "재고 수량은 필수입니다.")
        @Min(value = 0, message = "재고 수량은 0 이상 이어야 합니다.")
        private Integer stockQuantity;

        @NotNull(message = "안전 재고는 필수입니다.")
        @Min(value = 0, message = "안전 재고는 0 이상 이어야 합니다.")
        private Integer safetyStock;

        public SupplyItem toEntity() {
            return SupplyItem.builder()
                    .name(name)
                    .unit(unit)
                    .stockQuantity(stockQuantity)
                    .safetyStock(safetyStock)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "자재 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @NotNull(message = "재고 수량은 필수입니다.")
        @Min(value = 0, message = "재고 수량은 0 이상 이어야 합니다.")
        private Integer stockQuantity;

        @NotNull(message = "안전 재고는 필수입니다.")
        @Min(value = 0, message = "안전 재고는 0 이상 이어야 합니다.")
        private Integer safetyStock;
    }
}
