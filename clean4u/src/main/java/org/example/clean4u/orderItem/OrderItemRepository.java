package org.example.clean4u.orderItem;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.order.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {
    private final EntityManager em;

    // 주문(세탁) 품목 전체 조회
    public List<OrderItem> findAll(Order order) {
        return em.createQuery("SELECT i FROM OrderItem i WHERE i.order.id = i.id")
                .getResultList();
    }

    // 주문 품목 상세 조회
    public OrderItem findById(Long id) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        if (orderItem == null) {
            throw new Exception404("해당 주문 품목을 찾을 수 없습니다.");
        }
        return orderItem;
    }

    // 주문 품목 변경
    public OrderItem updateById(Long id, LaundryItem laundryItem, Integer quantity) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        if(orderItem == null) {
            throw new Exception404("해당 주문 품목을 찾을 수 없습니다.");
        }

        orderItem.updateOrderItem(laundryItem, quantity);
        return orderItem;
    }

    // 주문 품목 삭제
    public void deleteById(Long id) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        if(orderItem == null) {
            throw new Exception404("해당 주문 품목을 찾을 수 없습니다.");
        }
        em.remove(orderItem);
    }

}
