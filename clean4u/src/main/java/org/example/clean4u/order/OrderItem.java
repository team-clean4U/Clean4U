package org.example.clean4u.order;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item_tb")
public class OrderItem {
    // 하나의 Order 에 있는 구체적인 항목

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
