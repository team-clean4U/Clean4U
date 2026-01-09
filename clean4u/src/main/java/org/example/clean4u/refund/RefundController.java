package org.example.clean4u.refund;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.payment.Payment;
import org.example.clean4u.payment.PaymentResponse;
import org.example.clean4u.payment.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;
    private final PaymentService paymentService;

    @GetMapping("/refund/{paymentId}")
    public String refundRequestForm(@PathVariable Long paymentId, Model model) {
        Payment payment = refundService.refundRequestForm(paymentId);
        PaymentResponse.ListDTO paymentDTO = new PaymentResponse.ListDTO(payment);
        model.addAttribute("payment", paymentDTO);

        return "refund/request-form";
    }

//    @PostMapping("/refund")
//    public String refundRequest()
}
