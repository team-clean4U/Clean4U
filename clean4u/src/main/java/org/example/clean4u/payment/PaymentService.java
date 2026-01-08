package org.example.clean4u.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderStatus;

import org.example.clean4u.sms.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
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

    // 결제 준비 요청
    @Transactional
    public PaymentResponse.PrepareDTO preparePayment(PaymentRequest.@Valid PrepareDTO prepareDTO, Long sessionUserId) {
        if (!employeeRepository.existsById(sessionUserId)) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        Order order = orderRepository.findByIdWithCustomer(prepareDTO.getOrderId())
                .orElseThrow(() -> new Exception404("결제 시도하려는 주문을 찾을 수 없습니다."));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new Exception400("취소된 주문은 결제할 수 없습니다.");
        }

        String merchantUid = generateMerchantUid(prepareDTO.getOrderId());
        while (paymentRepository.existsByMerchantUid(merchantUid)) {
            merchantUid = generateMerchantUid(prepareDTO.getOrderId());
        }

        return new PaymentResponse.PrepareDTO(
                merchantUid,
                prepareDTO.getAmount()
        );
    }

    // 결제 검증 및 결제
    @Transactional
    public PaymentResponse.VerifyDTO verifyPayment(PaymentRequest.@Valid VerifyDTO reqDTO, Long userId) {
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        if (paymentRepository.findByImpUid(reqDTO.getImpUid()).isPresent()) {
            throw new Exception400("이미 처리된 결제 입니다");
        }

        PaymentResponse.PortOnePaymentResponse.PaymentData paymentData = findPortOnePayment(reqDTO.getImpUid(), reqDTO.getMerchantUid());

        Order order = orderRepository.findById(reqDTO.getOrderId())
                .orElseThrow(() -> new Exception404("주문 정보를 찾을 수 없습니다."));

        if (!order.getTotalPrice().equals(paymentData.getAmount())) {
            throw new Exception400("주문 총 금액과 결제 요청 금액이 일치하지 않습니다.");
        }

        order.updatePendingStatus(false);

        Payment payment = Payment.builder()
                .order(order)
                .impUid(reqDTO.getImpUid())
                .merchantUid(reqDTO.getMerchantUid())
                .amount(paymentData.getAmount())
                .paymentStatus(PaymentStatus.PAID)
                .build();

        paymentRepository.save(payment);
        return new PaymentResponse.VerifyDTO(paymentData.getAmount());
    }

    // 포트원 결제 조회
    private PaymentResponse.PortOnePaymentResponse.PaymentData findPortOnePayment(String impUid, String merchantUid) {
        String accessToken = generatePortOneAccessToken();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<PaymentResponse.PortOnePaymentResponse> response = restTemplate.exchange("https://api.iamport.kr/payments/" + impUid,
                    HttpMethod.GET,
                    request,
                    PaymentResponse.PortOnePaymentResponse.class);

            PaymentResponse.PortOnePaymentResponse.PaymentData data = response.getBody().getResponse();
            if (data == null) {
                throw new Exception400("결제 정보를 찾을 수 없습니다.");
            }

            if (!"paid".equals(data.getStatus())) {
                throw new Exception400("결제가 완료되지 않았습니다.");
            }

            if (!merchantUid.equals(data.getMerchantUid())) {
                throw new Exception400("주문 번호가 일치하지 않습니다.");
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 포트원 액세스 토큰 발급
    private String generatePortOneAccessToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = new HashMap<>();
            body.put("imp_key", impKey);
            body.put("imp_secret", impSecret);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<PaymentResponse.PortOneTokenResponse> response = restTemplate.exchange(
                    "https://api.iamport.kr/users/getToken",
                    HttpMethod.POST,
                    request,
                    PaymentResponse.PortOneTokenResponse.class
            );

            return response.getBody().getResponse().getAccessToken();
        } catch (Exception e) {
            throw new Exception400("포트원 인증 실패");
        }
    }

    private String generateMerchantUid(Long orderId) {
        return "order_" + orderId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

}
