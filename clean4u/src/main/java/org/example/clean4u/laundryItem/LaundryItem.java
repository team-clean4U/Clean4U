package org.example.clean4u.laundryItem;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "laundry_item_tb")
public class LaundryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private LaundryCategory category;

    @Column(nullable = false)
    private Integer basePrice;

    @Builder
    public LaundryItem(Long id, String name, LaundryCategory category, Integer basePrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
    }
}
