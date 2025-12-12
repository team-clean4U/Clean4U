package org.example.clean4u.customer;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomerPersistRepository {

    private final EntityManager em;

    // 저장
    public Customer save(Customer customer) {
        em.persist(customer);

        return customer;
    }

    // 전체 조회
    public List<Customer> findAll() {

        return em.createQuery("SELECT c FROM Customer c ORDER BY c.createdAt DESC")
                .getResultList();
    }

    // 단건 조회
    public Customer findById(Long id) {

        Customer customer = em.find(Customer.class, id);
        return customer;
    }

    // 수정
    public Customer updateById(Long id) {

        Customer customer = em.find(Customer.class, id);

        if (customer == null) {
            throw new IllegalArgumentException("해당 고객을 찾을 수 없습니다.");
        }

        
    }




}
