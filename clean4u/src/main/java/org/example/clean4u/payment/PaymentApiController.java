package org.example.clean4u.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentApiController {
    // TODO - 인터셉터 추가
    private final PaymentService paymentService;

    @PostMapping("/api/payment/prepare")
    public ResponseEntity<PaymentResponse.PrepareDTO> preparePayment(@RequestBody @Valid PaymentRequest.PrepareDTO reqDTO) {
        PaymentResponse.PrepareDTO prepDTO = paymentService.preparePayment(reqDTO);
        return ResponseEntity.ok(prepDTO);
    }

    @PostMapping("/api/payment/verify")
    public ResponseEntity<PaymentResponse.VerifyDTO> verifyPayment(@RequestBody @Valid PaymentRequest.VerifyDTO reqDTO) {
        PaymentResponse.VerifyDTO verifyDTO = paymentService.verifyPayment(reqDTO);
        return ResponseEntity.ok(verifyDTO);
    }
}
