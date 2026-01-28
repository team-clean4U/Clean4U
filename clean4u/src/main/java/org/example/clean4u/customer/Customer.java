package org.example.clean4u.customer;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@NoArgsConstructor
@Data
@Table(
        name = "customer_tb",
        indexes = {
                @Index(name = "idx_customer_name", columnList = "name"),
                @Index(name = "idx_customer_phone", columnList = "phone"),
                @Index(name = "idx_customer_grade", columnList = "grade")
        }
)
@Entity
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private Grade grade;

    @Column(name = "memo", length = 1000)
    private String memo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Employee createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private Employee updatedBy;

    @Builder
    public Customer(String name,
                    LocalDate birth,
                    String phone,
                    String memo,
                    Boolean isActive,
                    Employee createdBy) {
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.memo = memo;
        this.isActive = true;
        this.createdBy = createdBy;
    }

    public void update(CustomerRequest.UpdateDTO dto) {
        this.name = dto.getName();
        this.birth = dto.getBirth();
        this.phone = dto.getPhone();
        this.memo = dto.getMemo();
    }

    public void updateMemo(String newMemo) {
        this.memo = newMemo;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
