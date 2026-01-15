package org.example.clean4u.refund;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.payment.Payment;
import org.example.clean4u.payment.PaymentResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @GetMapping("/refunds/{paymentId}")
    public String refundRequestForm(@PathVariable Long paymentId, Model model) {
        Payment payment = refundService.refundRequestForm(paymentId);
        PaymentResponse.DetailDTO paymentDTO = new PaymentResponse.DetailDTO(payment);
        model.addAttribute("payment", paymentDTO);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/order.css", "/css/payment.css", "/css/refund.css", "/css/component.css"));
        return "refund/request-form";
    }
}
