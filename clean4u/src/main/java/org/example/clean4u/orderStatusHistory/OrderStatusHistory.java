package org.example.clean4u.orderStatusHistory;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(
        name = "order_status_history_tb",
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private Employee editor;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public OrderStatusHistory(Order order, OrderStatus status, Employee editor) {
        this.order = order;
        this.status = status;
        this.editor = editor;
    }
}
