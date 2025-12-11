package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.time.BaseTimeEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_tb")
@NoArgsConstructor
@Data
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(name = "order_item")
    private List<OrderItem> orderItem = new ArrayList<>();

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
    public Order(Long id, Customer customer, List<OrderItem> orderItem, OrderStatus status, Long totalPrice, LocalDate orderDate, String memo) {
        this.id = id;
        this.customer = customer;
        this.orderItem = orderItem;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.memo = memo;
    }

    public void updateOrder() {

    }
}
