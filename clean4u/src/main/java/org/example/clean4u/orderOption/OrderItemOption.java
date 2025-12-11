package org.example.clean4u.orderOption;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.order.OrderItem;
import org.example.clean4u.time.BaseTimeEntity;

@Entity
@Table(name = "order_item_option_tb")
@NoArgsConstructor
@Data
public class OrderItemOption extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laundry_item_id", nullable = false)
    private LaundryItem laundryItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Builder
    public OrderItemOption(Long id, LaundryItem laundryItem, OrderItem orderItem) {
        this.id = id;
        this.laundryItem = laundryItem;
        this.orderItem = orderItem;
    }
}
