package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.orderOption.OrderItemOption;
import org.example.clean4u.time.BaseTimeEntity;

import java.util.List;

@Entity
@Table(name = "order_item_tb")
@Data
@NoArgsConstructor
public class OrderItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laundry_item_id", nullable = false)
    private LaundryItem laundryItem;

    @OneToMany(mappedBy = "order_item_id", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemOption> orderItemOption;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public OrderItem(Long id, Order order, LaundryItem laundryItem, List<OrderItemOption> orderItemOption, Integer quantity) {
        this.id = id;
        this.order = order;
        this.laundryItem = laundryItem;
        this.orderItemOption = orderItemOption;
        this.quantity = quantity;
    }

    public void updateOrderItem() {

    }
}
