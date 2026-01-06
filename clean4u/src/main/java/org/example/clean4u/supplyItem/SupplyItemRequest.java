package org.example.clean4u.supplyItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.supplyItemHistory.Type;

import java.util.List;

public class SupplyItemRequest {

    @Data
    public static class SaveDTO {
        @NotNull(message = "유형은 필수입니다.")
        private Type historyType;

        private String memo;

        @NotEmpty(message = "비품은 필수입니다.")
        private List<ItemDTO> items;
    }

    @Data
    public static class ItemDTO {
        @NotBlank(message = "비품 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @NotNull(message = "재고 수량은 필수입니다.")
        @Min(value = 0, message = "재고 수량은 0 이상 이어야 합니다.")
        private Integer stockQuantity;

        @NotNull(message = "안전 재고는 필수입니다.")
        @Min(value = 0, message = "안전 재고는 0 이상 이어야 합니다.")
        private Integer safetyStock;

        @NotNull(message = "재고 활성 여부는 필수입니다.")
        private Boolean isActive;

        public SupplyItem toEntity() {
            return SupplyItem.builder()
                    .name(name)
                    .unit(unit)
                    .stockQuantity(stockQuantity)
                    .safetyStock(safetyStock)
                    .isActive(isActive)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "비품 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @NotNull(message = "재고 수량은 필수입니다.")
        @Min(value = 0, message = "재고 수량은 0 이상 이어야 합니다.")
        private Integer stockQuantity;

        @NotNull(message = "안전 재고는 필수입니다.")
        @Min(value = 0, message = "안전 재고는 0 이상 이어야 합니다.")
        private Integer safetyStock;

        @NotNull(message = "재고 활성 여부는 필수입니다.")
        private Boolean isActive;

        private Type historyType;
        
        @NotBlank(message = "메모는 필수입니다.")
        private String memo;
    }
}
