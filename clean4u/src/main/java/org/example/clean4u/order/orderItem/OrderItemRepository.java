package org.example.clean4u.order.orderItem;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.order.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {
    private final EntityManager em;

    @Transactional
    public OrderItem save(OrderItem orderItem) {
        em.persist(orderItem);
        return orderItem;
    }

    // 주문(세탁) 품목 전체 조회
    public List<OrderItem> findAllByOrderId(Long orderId) {
        return em.createQuery("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
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

    // 단일 주문 품목 변경
    @Transactional
    public OrderItem updateById(Long id, LaundryItem laundryItem, Integer quantity) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        if(orderItem == null) {
            throw new Exception404("해당 주문 품목을 찾을 수 없습니다.");
        }

        orderItem.updateOrderItem(laundryItem, quantity);
        return orderItem;
    }

    // 단일 주문 품목 삭제
    @Transactional
    public void deleteById(Long id) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        if(orderItem == null) {
            throw new Exception404("해당 주문 품목을 찾을 수 없습니다.");
        }
        em.remove(orderItem);
    }

    // 주문내에 있는 모든 주문 품목 삭제
    @Transactional
    public void deleteByOrderId(Long orderId) {
        int deletedCount = em.createQuery("DELETE FROM OrderItem i WHERE i.order.id = :orderId")
                .setParameter("orderId", orderId)
                .executeUpdate();

        if(deletedCount == 0) {
            throw new Exception404("해당 주문의 주문 품목을 찾을 수 없습니다.");
        }
    }
}
