package org.example.clean4u.supplyItem;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SupplyItemRepository {

    private final EntityManager em;

    @Transactional
    public SupplyItem save(SupplyItem supplyItem) {
        em.persist(supplyItem);
        return supplyItem;
    }

    public List<SupplyItem> findAll() {
        return em.createQuery(
                "SELECT si FROM SupplyItem si ORDER BY si.id DESC",
                SupplyItem.class
        ).getResultList();
    }

    public SupplyItem findById(Long id) {
        return em.find(SupplyItem.class, id);
    }

    @Transactional
    public SupplyItem updateById(Long id, SupplyItemRequest.UpdateDTO updateDTO) {
        SupplyItem supplyItem = em.find(SupplyItem.class, id);
        if (supplyItem == null) {
            throw new IllegalArgumentException("수정할 자재가 없습니다.");
        }
        supplyItem.update(updateDTO);
        return supplyItem;
    }

    @Transactional
    public void deleteById(Long id) {
        SupplyItem supplyItem = em.find(SupplyItem.class, id);
        if (supplyItem == null) {
            throw new IllegalArgumentException("삭제할 자재가 없습니다.");
        }
        em.remove(supplyItem);
    }
}
