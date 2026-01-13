package org.example.clean4u.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentApiController {
    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse<PaymentResponse.PrepareDTO>> preparePayment(@RequestBody @Valid PaymentRequest.PrepareDTO reqDTO) {
        PaymentResponse.PrepareDTO prepDTO = paymentService.preparePayment(reqDTO);
        return ResponseEntity.ok(ApiResponse.ok(prepDTO));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse.VerifyDTO>> verifyPayment(@RequestBody @Valid PaymentRequest.VerifyDTO reqDTO) {
        PaymentResponse.VerifyDTO verifyDTO = paymentService.verifyPayment(reqDTO);
        return ResponseEntity.ok(ApiResponse.ok(verifyDTO));
    }
}
