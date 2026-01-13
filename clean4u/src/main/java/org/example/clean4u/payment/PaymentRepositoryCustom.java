package org.example.clean4u.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepositoryCustom {
    Page<Payment> searchPayments(Pageable pageable, PaymentRequest.SearchDTO searchDTO);
}
