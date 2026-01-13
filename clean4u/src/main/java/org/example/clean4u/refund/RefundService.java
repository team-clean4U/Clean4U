package org.example.clean4u.refund;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.errors.exception.Exception500;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderStatus;
import org.example.clean4u.payment.Payment;
import org.example.clean4u.payment.PaymentRepository;
import org.example.clean4u.payment.PaymentService;
import org.example.clean4u.payment.PaymentStatus;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public Payment refundRequestForm(Long paymentId) {
        Payment payment = paymentRepository.findByIdWithOrder(paymentId)
                .orElseThrow(() -> new Exception404("결제 내역을 조회할 수 없습니다."));

        if (!(payment.isPaid())) {
            throw new Exception400("결제 완료된 상태만 환불 요청이 가능합니다.");
        }

        if (!payment.getOrder().isReceived()) {
            throw new Exception400("접수 단계의 주문만 환불 요청이 가능합니다.");
        }

        Refund refund = refundRepository.findByPaymentId(paymentId).orElse(null);
        if (refund != null && refund.isApproved()) {
            throw new Exception400("이미 환불 처리된 주문입니다.");
        }

        return payment;
    }


    @Transactional
    public void refundRequestProc(Long paymentId, RefundRequest.DetailDTO detailDTO) {
        Payment payment = refundRequestForm(paymentId);

        Order order = orderRepository.findById(payment.getOrder().getId())
                .orElseThrow(() -> new Exception404("주문정보를 찾을 수 없습니다."));

        Refund refund = Refund.builder()
                .payment(payment)
                .reason(detailDTO.getReason())
                .status(RefundStatus.PENDING)
                .build();

        refundRepository.save(refund);

        portOnePayCancel(payment.getImpUid(), refund.getPayment().getAmount());

        // 예외 없이 포트원 결제 취소가 성공 되면
        refund.approve();
        payment.updateStatus(PaymentStatus.REFUND);
        order.updatePendingStatus(true); // 주문의 결제 상태는 완료 -> 대기로 변경
        order.updateStatus(OrderStatus.CANCELLED); // 접수 취소
    }

    private void portOnePayCancel(String impUid, Integer amount) {
        String accessToken = paymentService.generatePortOneAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = new HashMap<>();
        Payment payment = paymentRepository.findByImpUid(impUid)
                .orElseThrow(() -> new Exception404("결제 정보를 찾을 수 없습니다."));
        Refund refund = refundRepository.findByPaymentId(payment.getId())
                .orElseThrow(() -> new Exception404("환불 요청 정보를 찾을 수 없습니다."));

        body.put("reason", refund.getReason());
        body.put("imp_uid", impUid);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<RefundResponse.PortOneRefundApprove> response
                    = restTemplate.exchange(
                    "https://api.iamport.kr/payments/cancel",
                    HttpMethod.POST,
                    requestEntity,
                    RefundResponse.PortOneRefundApprove.class
            );
            RefundResponse.PortOneRefundApprove responseBody = response.getBody();
            if (responseBody == null) {
                throw new Exception500("포트원 결제취소 응답이 비어있습니다.");
            }
            if (responseBody.getCode() != 0) {
                refund.setStatus(RefundStatus.CANCELLED);
                throw new Exception500("환불에 실패하였습니다.");
            }
        } catch (Exception e) {
            refund.setStatus(RefundStatus.CANCELLED);
            throw new Exception500("포트원 결제 취소중 오류가 발생하였습니다.");
        }
    }
}
