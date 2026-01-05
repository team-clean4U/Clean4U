package org.example.clean4u.payment;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentApiController {
    private final PaymentService paymentService;

    @PostMapping("/api/payment/prepare")
    public ResponseEntity<PaymentResponse.PrepareDTO> preparePayment(@RequestBody @Valid PaymentRequest.PrepareDTO reqDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if(sessionUser == null) {
            return ResponseEntity.status(401).body(null);
        }
        PaymentResponse.PrepareDTO prepDTO = paymentService.preparePayment(reqDTO, sessionUser.getId());

        return ResponseEntity.ok(prepDTO);
    }

    @PostMapping("/api/payment/verify")
    public ResponseEntity<PaymentResponse.VerifyDTO> verifyPayment(@RequestBody @Valid PaymentRequest.VerifyDTO reqDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if(sessionUser == null) {
            return ResponseEntity.status(401).body(null);
        }

        PaymentResponse.VerifyDTO verifyDTO = paymentService.verifyPayment(reqDTO, sessionUser.getId());
        return ResponseEntity.ok(verifyDTO);
    }
}
