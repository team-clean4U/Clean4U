package org.example.clean4u.laundryOption;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LaundryOptionRepository {

    private final EntityManager em;

    @Transactional
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
        LaundryOption laundryOption = em.find(LaundryOption.class, id);
        if (laundryOption == null) {
            throw new Exception404("해당 세탁물 옵션을 찾을 수 없습니다.");
        }
        return laundryOption;
    }

    @Transactional
    public LaundryOption updateById(Long id, LaundryOptionRequest.UpdateDTO updateDTO) {
        LaundryOption laundryOption = em.find(LaundryOption.class, id);
        if (laundryOption == null) {
            throw new Exception404("해당 세탁물 옵션을 찾을 수 없습니다.");
        }
        laundryOption.update(updateDTO);
        return laundryOption;
    }

    @Transactional
    public void deleteById(Long id) {
        LaundryOption laundryOption = em.find(LaundryOption.class, id);
        if (laundryOption == null) {
            throw new Exception404("해당 세탁물 옵션을 찾을 수 없습니다.");
        }
        em.remove(laundryOption);
    }
}
