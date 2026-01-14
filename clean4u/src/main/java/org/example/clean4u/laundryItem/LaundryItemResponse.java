package org.example.clean4u.laundryItem;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;
import org.example.clean4u._core.utils.PriceUtil;

public class LaundryItemResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String name;
        private LaundryCategory category;
        private String basePrice;
        private String description;
        private String icon;
        private Boolean isActive;

        public ListDTO(LaundryItem laundryItem) {
            this.id = laundryItem.getId();
            this.name = laundryItem.getName();
            this.category = laundryItem.getCategory();
            this.basePrice = laundryItem.getBasePrice() != null ? PriceUtil.format(laundryItem.getBasePrice()) : null ;
            this.description = laundryItem.getDescription();
            this.icon = laundryItem.getIcon();
            this.isActive = laundryItem.getIsActive();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String name;
        private LaundryCategory category;
        private String basePrice;
        private String description;
        private String createdAt;
        private String updatedAt;
        private String icon;
        private Boolean isActive;
        private Boolean hasOrder;

        public DetailDTO(LaundryItem laundryItem) {
            this.id = laundryItem.getId();
            this.name = laundryItem.getName();
            this.category = laundryItem.getCategory();
            this.basePrice = laundryItem.getBasePrice() != null ? PriceUtil.format(laundryItem.getBasePrice()) : null;
            this.description = laundryItem.getDescription();
            this.createdAt = DateUtil.timestampFormat(laundryItem.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(laundryItem.getUpdatedAt());
            this.icon = laundryItem.getIcon();
            this.isActive = laundryItem.getIsActive();
            this.hasOrder = false;
        }

        public DetailDTO(LaundryItem laundryItem, boolean hasOrder) {
            this.id = laundryItem.getId();
            this.name = laundryItem.getName();
            this.category = laundryItem.getCategory();
            this.basePrice = laundryItem.getBasePrice() != null ? PriceUtil.format(laundryItem.getBasePrice()) : null;
            this.description = laundryItem.getDescription();
            this.createdAt = DateUtil.timestampFormat(laundryItem.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(laundryItem.getUpdatedAt());
            this.icon = laundryItem.getIcon();
            this.isActive = laundryItem.getIsActive();
            this.hasOrder = hasOrder;
        }
    }

    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String name;
        private LaundryCategory category;
        private Integer basePrice;
        private String description;
        private String icon;
        private Boolean isActive;

        public UpdateFormDTO(LaundryItem laundryItem) {
            this.id = laundryItem.getId();
            this.name = laundryItem.getName();
            this.category = laundryItem.getCategory();
            this.basePrice = laundryItem.getBasePrice();
            this.description = laundryItem.getDescription();
            this.icon = laundryItem.getIcon();
            this.isActive = laundryItem.getIsActive();
        }
    }
}
