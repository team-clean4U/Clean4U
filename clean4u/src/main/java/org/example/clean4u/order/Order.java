package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.time.BaseTimeEntity;

import java.time.LocalDate;

@Entity
@Table(name = "order_tb")
@NoArgsConstructor
@Data
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "memo", length = 50)
    private String memo;

    @Builder
    public Order(Long id, Customer customer, OrderStatus status, Long totalPrice, LocalDate orderDate, String memo) {
        this.id = id;
        this.customer = customer;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate == null ? LocalDate.now() : orderDate;
        this.memo = memo;
    }

    public void updateOrder(OrderRequest.UpdateDto updateDto) {
        updateDto.validate();
        this.status = updateDto.getStatus();
        this.totalPrice = updateDto.getTotalPrice();
        this.orderDate = updateDto.orderDate == null ? LocalDate.now() : updateDto.orderDate;
        this.memo = updateDto.getMemo();
    }

    public void updateStatus(OrderStatus newStatus) {
        if(newStatus == null) {
            throw new IllegalArgumentException("주문 상태는 null일 수 없습니다.");
        }
        this.status = newStatus;
    }
}
