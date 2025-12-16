package org.example.clean4u.supplyItem;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.time.BaseTimeEntity;

@NoArgsConstructor
@Data
@Entity
@Table(name = "supply_item_tb")
public class SupplyItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_item_id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Builder
    public SupplyItem(Long id, String name, String unit, Integer stockQuantity, Integer safetyStock) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.safetyStock = safetyStock;
    }

    public void update(SupplyItemRequest.UpdateDTO updateDTO) {
        this.name = updateDTO.getName();
        this.unit = updateDTO.getUnit();
        this.stockQuantity = updateDTO.getStockQuantity();
        this.safetyStock = updateDTO.getSafetyStock();
    }

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("자재 이름은 필수입니다.");
        }
        this.name = newName;
    }

    public void updateUnit(String newUnit) {
        if (newUnit == null || newUnit.trim().isEmpty()) {
            throw new IllegalArgumentException("단위는 필수입니다.");
        }
        this.unit = newUnit;
    }

    public void updateStockQuantity(Integer newStockQuantity) {
        if (newStockQuantity == null || newStockQuantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상 이어야 합니다.");
        }
        this.stockQuantity = newStockQuantity;
    }

    public void updateSafetyStock(Integer newSafetyStock) {
        if (newSafetyStock == null || newSafetyStock < 0) {
            throw new IllegalArgumentException("안전 재고는 0 이상 이어야 합니다.");
        }
        this.safetyStock = newSafetyStock;
    }
}
