package org.example.clean4u.laundryItem;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(
        name = "laundry_item_tb",
        indexes = {
                @Index(name = "idx_laundry_item_category", columnList = "category"),
                @Index(name = "idx_laundry_item_created_at", columnList = "created_at")
        }
)
public class LaundryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "laundry_item_id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private LaundryCategory category;

    @Column(name = "base_price", nullable = false)
    private Integer basePrice;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public LaundryItem(String name, LaundryCategory category, Integer basePrice, String description) {
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
        this.description = description;
    }

    public void update(LaundryItemRequest.UpdateDTO updateDTO) {
        this.name = updateDTO.getName();
        this.category = updateDTO.getCategory();
        this.basePrice = updateDTO.getBasePrice();
        this.description = updateDTO.getDescription();
    }

    public String getIcon() {
        return category != null ? category.getIcon() : "box-open";
    }
}
