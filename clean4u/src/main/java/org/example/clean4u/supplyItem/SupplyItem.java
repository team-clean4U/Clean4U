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
}
