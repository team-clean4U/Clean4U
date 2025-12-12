package org.example.clean4u.laundryOption;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LaundryOptionRepository {

    private final EntityManager em;

    public LaundryOption save(LaundryOption laundryOption) {
        em.persist(laundryOption);
        return laundryOption;
    }

    public List<LaundryOption> findAll() {
        return em.createQuery(
                "SELECT lo FROM LaundryOption lo ORDER BY id DESC",
                LaundryOption.class
        ).getResultList();
    }

    public LaundryOption findById(Long id) {
        return em.find(LaundryOption.class, id);
    }

    @Transactional
    public LaundryOption update(Long id, LaundryOptionRequest.UpdateDTO updateDTO) {
        LaundryOption laundryOption = em.find(LaundryOption.class, id);
        if (laundryOption == null) {
            throw new IllegalArgumentException("수정할 옵션이 없습니다.");
        }
        laundryOption.update(updateDTO);
        return laundryOption;
    }

    @Transactional
    public void delete(Long id) {
        LaundryOption laundryOption = em.find(LaundryOption.class, id);
        if (laundryOption == null) {
            throw new IllegalArgumentException("삭제할 옵션이 없습니다.");
        }
        em.remove(laundryOption);
    }
}
