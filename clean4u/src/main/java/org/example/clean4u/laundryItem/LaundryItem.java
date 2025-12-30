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

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("세탁물 이름은 필수입니다.");
        }
        this.name = newName;
    }

    public void updateCategory(LaundryCategory newCategory) {
        if (newCategory == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
        this.category = newCategory;
    }

    public void updateBasePrice(Integer newBasePrice) {
        if (newBasePrice == null || newBasePrice < 0) {
            throw new IllegalArgumentException("기본 요금은 0 이상이어야 합니다.");
        }
        this.basePrice = newBasePrice;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public String getIcon() {
        return category != null ? category.getIcon() : "box-open";
    }
}
