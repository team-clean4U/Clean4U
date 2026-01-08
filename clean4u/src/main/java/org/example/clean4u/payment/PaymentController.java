package org.example.clean4u.payment;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @GetMapping("/payment/{orderId}/{merchantUid}")
    public String payPage(@PathVariable Long orderId, @PathVariable String merchantUid, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        if(!order.isPending()) {
            throw new Exception400("이미 결제된 주문입니다.");
        }

        Customer customer = customerRepository.findById(order.getCustomer().getId())
                        .orElseThrow(() -> new Exception404("해당 고객이 존재하지 않습니다."));

        model.addAttribute("merchantUid", merchantUid);
        model.addAttribute("customer", customer);
        model.addAttribute("amount", order.getTotalPrice());
        model.addAttribute("orderId", orderId);
        return "payment/pay";
    }

}
