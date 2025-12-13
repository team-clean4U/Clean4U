package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    // 주문 전체 조회
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.createdAt DESC", Order.class)
                .getResultList();
    }

    // 주문 상세 조회
    public Order findById(Long id) {
        Order order = em.find(Order.class, id);
        if(order == null) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }
        return order;
    }

    // 주문 생성(저장)
    @Transactional
    public Order save(Order order) {
        em.persist(order);
        return order;
    }

    // 주문 수정
    @Transactional
    public Order updateById(Long id, OrderRequest.UpdateDto updateDto) {
        Order order = em.find(Order.class, id);

        if(order == null) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }

        order.updateOrder(updateDto);
        return order;
    }

    // 주문 삭제
    @Transactional
    public void deleteById(Long id) {
        Order order = em.find(Order.class, id);

        if(order == null) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }

        em.remove(order);
    }

}
