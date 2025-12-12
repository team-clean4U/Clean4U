package org.example.clean4u.laundryItem;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LaundryItemRepository {

    private final EntityManager em;

    @Transactional
    public LaundryItem save(LaundryItem laundryItem) {
        em.persist(laundryItem);
        return laundryItem;
    }

    public List<LaundryItem> findAll() {
        return em.createQuery(
                        "SELECT li FROM LaundryItem li ORDER BY li.id DESC"
                        , LaundryItem.class)
                .getResultList();
    }

    public LaundryItem findById(Long id) {
        return em.find(LaundryItem.class, id);
    }

    @Transactional
    public LaundryItem updateById(Long id, LaundryItemRequest.UpdateDTO reqDTO) {
        LaundryItem laundryItem = em.find(LaundryItem.class, id);
        if (laundryItem == null) {
            throw new IllegalArgumentException("수정할 세탁물을 찾을 수 없습니다.");
        }
        laundryItem.update(reqDTO);
        return laundryItem;
    }

    @Transactional
    public void deleteById(Long id) {
        LaundryItem laundryItem = em.find(LaundryItem.class, id);
        if (laundryItem == null) {
            throw new IllegalArgumentException("삭제할 세탁물을 찾을 수 없습니다.");
        }
        em.remove(laundryItem);
    }
}
