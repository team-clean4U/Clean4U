package org.example.clean4u.supplyItem;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "supply_item_tb")
public class SupplyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Builder
    public SupplyItem(Long id, String name, String unit, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
    }

    public void update(SupplyItemRequest.UpdateDTO updateDTO) {
        this.name = updateDTO.getName();
        this.unit = updateDTO.getUnit();
        this.stockQuantity = updateDTO.getStockQuantity();
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
}
