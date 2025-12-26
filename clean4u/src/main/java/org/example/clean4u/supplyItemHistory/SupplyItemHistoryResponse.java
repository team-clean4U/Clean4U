package org.example.clean4u.supplyItemHistory;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SupplyItemHistoryResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String type;
        private String typeDescription;
        private String firstSupplyItemName;
        private Integer itemCount;
        private String employeeName;
        private String createdAt;
        private String displayText;

        public ListDTO(SupplyItemHistory supplyItemHistory, Integer itemCount) {
            this.id = supplyItemHistory.getId();
            this.type = supplyItemHistory.getType().name();
            this.typeDescription = supplyItemHistory.getType().getDescription();
            this.firstSupplyItemName = supplyItemHistory.getSupplyItem().getName();
            this.itemCount = itemCount;
            this.employeeName = supplyItemHistory.getEmployee().getName();
            this.createdAt = DateUtil.timestampFormat(supplyItemHistory.getCreatedAt());

            if (itemCount > 1) {
                this.displayText = firstSupplyItemName + " 외 " + (itemCount - 1) + "건";
            } else {
                this.displayText = firstSupplyItemName;
            }
        }
    }

    @Data
    public static class GroupDetailDTO {
        private Long id;
        private String type;
        private String typeDescription;
        private String employeeName;
        private String createdAt;
        private String memo;
        private Integer itemCount;
        private List<ItemDetailDTO> items;

        public GroupDetailDTO(SupplyItemHistory fistItemHistory, List<SupplyItemHistory> groupedHistories) {
            this.id = fistItemHistory.getId();
            this.type = fistItemHistory.getType().name();
            this.typeDescription = fistItemHistory.getType().getDescription();
            this.employeeName = fistItemHistory.getEmployee().getName();
            this.createdAt = DateUtil.timestampFormat(fistItemHistory.getCreatedAt());
            this.memo = fistItemHistory.getMemo();
            this.items = groupedHistories.stream()
                    .map(ItemDetailDTO::new)
                    .collect(Collectors.toList());
            this.itemCount = this.items.size();
        }
    }

    @Data
    public static class ItemDetailDTO {
        private Long id;
        private Long supplyItemId;
        private String supplyItemName;
        private Integer quantity;
        private Integer stockBefore;
        private Integer stockAfter;

        public ItemDetailDTO(SupplyItemHistory supplyItemHistory) {
            this.id = supplyItemHistory.getId();
            this.supplyItemId = supplyItemHistory.getSupplyItem().getId();
            this.supplyItemName = supplyItemHistory.getSupplyItem().getName();
            this.quantity = supplyItemHistory.getQuantity();
            this.stockBefore = supplyItemHistory.getStockBefore();
            this.stockAfter = supplyItemHistory.getStockAfter();
        }
    }

}
