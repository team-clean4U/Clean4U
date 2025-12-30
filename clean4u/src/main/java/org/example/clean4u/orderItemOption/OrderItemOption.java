package org.example.clean4u.orderItemOption;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.laundryOption.LaundryOption;
import org.example.clean4u.orderItem.OrderItem;
import org.example.clean4u.time.BaseTimeEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(
        name = "order_item_option_tb",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_laundry_option", columnNames = {"order_item_id", "laundry_option_id"})
        }
)
@NoArgsConstructor
@Data
public class OrderItemOption{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laundry_option_id", nullable = false)
    private LaundryOption laundryOption;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public OrderItemOption(LaundryOption laundryOption, OrderItem orderItem) {
        this.laundryOption = laundryOption;
        this.orderItem = orderItem;
    }

}
