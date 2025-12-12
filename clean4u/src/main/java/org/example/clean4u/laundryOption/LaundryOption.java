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
}
