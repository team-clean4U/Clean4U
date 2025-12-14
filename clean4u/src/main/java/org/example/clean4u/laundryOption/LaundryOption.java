package org.example.clean4u.laundryOption;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "laundry_option_tb")
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

    @Builder
    public LaundryOption(Long id, String name, Integer extraPrice, String description, Boolean isActive) {
        this.id = id;
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

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 이름은 필수입니다.");
        }
        this.name = newName;
    }

    public void updateExtraPrice(Integer newExtraPrice) {
        if (newExtraPrice == null || newExtraPrice < 0) {
            throw new IllegalArgumentException("추가 요금은 0 이상이어야 합니다.");
        }
        this.extraPrice = newExtraPrice;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateIsActive(Boolean newIsActive) {
        this.isActive = newIsActive;
    }
}
