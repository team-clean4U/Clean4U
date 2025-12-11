package org.example.clean4u.orderItem;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.order.Order;
import org.example.clean4u.time.BaseTimeEntity;

@Entity
@Table(name = "order_item_tb")
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
    public OrderItem(Long id, Order order, LaundryItem laundryItem, Integer quantity) {
        this.id = id;
        this.order = order;
        this.laundryItem = laundryItem;
        this.quantity = quantity;
    }

    public void updateOrderItem(OrderItemRequest.UpdateDto updateDto) {
        updateDto.validate();
        this.laundryItem = updateDto.getLaundryItem();
        this.quantity = updateDto.getQuantity();
    }
}
