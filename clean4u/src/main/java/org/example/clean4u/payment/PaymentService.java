package org.example.clean4u.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.employee.EmployeeRepository;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;

    @Transactional
    // 결제 준비 요청
    public PaymentResponse.PrepareDTO preparePayment(PaymentRequest.@Valid PrepareDTO prepareDTO, Long sessionUserId) {
        if(!employeeRepository.existsById(sessionUserId)) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        Order order = orderRepository.findById(prepareDTO.getOrderId())
                        .orElseThrow(() -> new Exception404("결제 시도하려는 주문을 찾을 수 없습니다."));

        if(order.getStatus() == OrderStatus.CANCELLED) {
            throw new Exception400("취소된 주문은 결제할 수 없습니다.");
        }

        Optional<Payment> optional = paymentRepository.findByOrderId(prepareDTO.getOrderId());
        if(optional.isPresent()) {
            Payment payment = optional.get();
            if(payment.getPaymentStatus() == PaymentStatus.PAID ) {
                throw new Exception400("이미 결제 완료된 주문입니다.");
            }

            // 결제 대기 상태면 이전 결제 준비 요청 정보 그대로 사용
            if(payment.getPaymentStatus() == PaymentStatus.PENDING) {
                return new PaymentResponse.PrepareDTO(
                        payment.getMerchantUid(),
                        payment.getAmount(),
                        impKey
                );
            }
        }

        if(!order.getTotalPrice().equals(prepareDTO.getAmount())) {
            throw new Exception400("결제 예정 금액과 결제 요청 금액이 불일치합니다.");
        }

        String merchantUid = generateMerchantUid(prepareDTO.getOrderId());
        while (paymentRepository.existsByMerchantUid(merchantUid)) {
            merchantUid = generateMerchantUid(prepareDTO.getOrderId());
        }

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .impUid(null) // impUid: 결제 완료되어야 포트원 서버에서 impUid 내려줌
                        .merchantUid(merchantUid)
                        .amount(prepareDTO.getAmount())
                        .paymentStatus(PaymentStatus.PENDING)
                        .build()
        );

        return new PaymentResponse.PrepareDTO(
                payment.getMerchantUid(),
                payment.getAmount(),
                impKey
        );
    }

    private String generateMerchantUid(Long orderId) {
        return "order_" + orderId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
