package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(
        name = "order_status_history",
        indexes = {
                @Index(name = "idx_order_status_history_order", columnList = "order_id"),
                @Index(name = "idx_order_status_history_createdAt", columnList = "created_at")
        }
)
@NoArgsConstructor
@Data
public class OrderStatusHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public OrderStatusHistory(Order order, OrderStatus status) {
        this.order = order;
        this.status = status;
    }
}
