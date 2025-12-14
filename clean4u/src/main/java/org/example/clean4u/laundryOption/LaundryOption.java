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
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer extraPrice;

    @Builder
    public LaundryOption(Long id, String name, Integer extraPrice) {
        this.id = id;
        this.name = name;
        this.extraPrice = extraPrice;
    }

    public void update(LaundryOptionRequest.UpdateDTO updateDTO) {
        this.name = updateDTO.getName();
        this.extraPrice = updateDTO.getExtraPrice();
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
}
