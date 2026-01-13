package org.example.clean4u.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/payments")
    public String paymentList(Model model,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "9") int size,
                                                @ModelAttribute PaymentRequest.SearchDTO searchDTO,
                                                HttpServletRequest request) {
        int pageIndex = Math.max(0, page - 1);

        String queryString = request.getQueryString();
        if(queryString != null) {
            queryString = queryString.replaceAll("(page=\\d+)", "");
            queryString = queryString.replaceAll("(&size=\\d+)", "");
            if(!queryString.isBlank()) {
                queryString = "&" + queryString;
            }
        }

        PageResponse<PaymentResponse.ListDTO> paymentListPage = paymentService.paymentList(pageIndex, size, searchDTO);
        model.addAttribute("paymentList", paymentListPage.getContent());
        model.addAttribute("paymentListPage", paymentListPage);
        model.addAttribute("searchView", searchDTO);
        model.addAttribute("queryString", queryString);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/order.css", "/css/payment.css", "/css/supply-item-history.css"));

        return "payment/list-form";
    }
}
