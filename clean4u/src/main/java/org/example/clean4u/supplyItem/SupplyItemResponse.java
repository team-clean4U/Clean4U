package org.example.clean4u.supplyItem;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;

public class SupplyItemResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String name;
        private String unit;
        private Integer stockQuantity;
        private Integer safetyStock;
        private Boolean isLowStock;
        private Boolean isActive;

        public ListDTO(SupplyItem supplyItem) {
            this.id = supplyItem.getId();
            this.name = supplyItem.getName();
            this.unit = supplyItem.getUnit();
            this.stockQuantity = supplyItem.getStockQuantity();
            this.safetyStock = supplyItem.getSafetyStock();
            this.isLowStock = supplyItem.getStockQuantity() <= supplyItem.getSafetyStock();
            this.isActive = supplyItem.getIsActive();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String name;
        private String unit;
        private Integer stockQuantity;
        private Integer safetyStock;
        private Boolean isLowStock;
        private Boolean isActive;
        private String createdAt;
        private String updatedAt;

        public DetailDTO(SupplyItem supplyItem) {
            this.id = supplyItem.getId();
            this.name = supplyItem.getName();
            this.unit = supplyItem.getUnit();
            this.stockQuantity = supplyItem.getStockQuantity();
            this.safetyStock = supplyItem.getSafetyStock();
            this.isLowStock = supplyItem.getStockQuantity() <= supplyItem.getSafetyStock();
            this.isActive = supplyItem.getIsActive();
            this.createdAt = DateUtil.timestampFormat(supplyItem.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(supplyItem.getUpdatedAt());
        }
    }

    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String name;
        private String unit;
        private Integer stockQuantity;
        private Integer safetyStock;
        private Boolean isActive;

        public UpdateFormDTO(SupplyItem supplyItem) {
            this.id = supplyItem.getId();
            this.name = supplyItem.getName();
            this.unit = supplyItem.getUnit();
            this.stockQuantity = supplyItem.getStockQuantity();
            this.safetyStock = supplyItem.getSafetyStock();
            this.isActive = supplyItem.getIsActive();
        }
    }
}
