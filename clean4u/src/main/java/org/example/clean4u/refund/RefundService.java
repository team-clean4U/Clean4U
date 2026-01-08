package org.example.clean4u.refund;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.payment.Payment;
import org.example.clean4u.payment.PaymentRepository;
import org.example.clean4u.payment.PaymentStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    public Payment refundRequestForm(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new Exception404("결제 내역을 조회할 수 없습니다."));

        if(!(payment.getPaymentStatus() == PaymentStatus.PAID)) {
            throw new Exception400("결제 완료된 상태만 환불 요청이 가능합니다.");
        }

        if(refundRepository.findByPaymentId(paymentId).isPresent()) {
            throw new Exception400("이미 환불 요청이 진행중입니다.");
        }

        return payment;
    }
}
