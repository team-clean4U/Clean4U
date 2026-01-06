package org.example.clean4u.laundryOption;

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
        name = "laundry_option_tb",
        indexes = {
                @Index(name = "idx_laundry_option_is_active", columnList = "is_active"),
                @Index(name = "idx_laundry_option_created_at", columnList = "created_at")
        }
)
public class LaundryOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "laundry_option_id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "extra_price", nullable = false)
    private Integer extraPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public LaundryOption(String name, Integer extraPrice, String description, Boolean isActive) {
        this.name = name;
        this.extraPrice = extraPrice;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(LaundryOptionRequest.UpdateDTO updateDTO) {
        this.name = updateDTO.getName();
        this.extraPrice = updateDTO.getExtraPrice();
        this.description = updateDTO.getDescription();
        this.isActive = updateDTO.getIsActive();
    }

    public void updateIsActive(Boolean newIsActive) {
        this.isActive = newIsActive;
    }
}
