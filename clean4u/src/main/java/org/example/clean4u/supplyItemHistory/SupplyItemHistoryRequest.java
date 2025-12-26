package org.example.clean4u.supplyItemHistory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

public class SupplyItemHistoryRequest {
    @Data
    public static class SaveDTO {
        @NotNull(message = "거래 유형은 필수입니다.")
        private Type type;

        @NotEmpty(message = "비품을 선택하세요.")
        @Valid
        private List<ItemDTO> items;

        private String memo;
    }

    @Data
    public static class ItemDTO {
        @NotNull(message = "비품 ID는 필수입니다.")
        private Long supplyItemId;

        @NotNull(message = "수량은 필수입니다.")
        private Integer quantity;
    }

}
