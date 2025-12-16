package org.example.clean4u.laundryOption;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;

public class LaundryOptionResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String name;
        private Integer extraPrice;
        private String description;
        private Boolean isActive;

        public ListDTO(LaundryOption laundryOption) {
            this.id = laundryOption.getId();
            this.name = laundryOption.getName();
            this.extraPrice = laundryOption.getExtraPrice();
            this.description = laundryOption.getDescription();
            this.isActive = laundryOption.getIsActive();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String name;
        private Integer extraPrice;
        private String description;
        private Boolean isActive;
        private String createdAt;
        private String updatedAt;

        public DetailDTO(LaundryOption laundryOption) {
            this.id = laundryOption.getId();
            this.name = laundryOption.getName();
            this.extraPrice = laundryOption.getExtraPrice();
            this.description = laundryOption.getDescription();
            this.isActive = laundryOption.getIsActive();
            this.createdAt = DateUtil.timestampFormat(laundryOption.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(laundryOption.getUpdatedAt());
        }
    }

    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String name;
        private Integer extraPrice;
        private String description;
        private Boolean isActive;

        public UpdateFormDTO(LaundryOption laundryOption) {
            this.id = laundryOption.getId();
            this.name = laundryOption.getName();
            this.extraPrice = laundryOption.getExtraPrice();
            this.description = laundryOption.getDescription();
            this.isActive = laundryOption.getIsActive();
        }
    }
}
