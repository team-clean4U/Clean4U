package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.time.BaseTimeEntity;

@Entity
@Table(
        name = "order_item_tb",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_laundry_item", columnNames = {"order_id", "laundry_item_id"})
        }
)
@Data
@NoArgsConstructor
public class OrderItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laundry_item_id", nullable = false)
    private LaundryItem laundryItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public OrderItem(Order order, LaundryItem laundryItem, Integer quantity) {
        this.order = order;
        this.laundryItem = laundryItem;
        this.quantity = quantity;
    }

    public void updateOrderItem(LaundryItem laundryItem, Integer quantity) {
        this.laundryItem = laundryItem;
        this.quantity = quantity;
    }
}
