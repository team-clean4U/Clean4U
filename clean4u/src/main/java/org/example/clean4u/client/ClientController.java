package org.example.clean4u.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.order.OrderResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ClientController {

    private final ClientService clientService;

    // http://localhost:8080/client
    @GetMapping("/client")
    public String clientMain(Model model) {
        model.addAttribute("additionalCss", List.of("/css/client.css"));
        return "client/main";
    }

    // http://localhost:8080/client/{phone}/orders
    @GetMapping("/client/{phone}/orders")
    public String orderList(
            @PathVariable String phone,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model,
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

        PageResponse<OrderResponse.ListDTO> orderListPage = clientService.getOrderListByPhone(phone, pageIndex, size);
        ClientResponse.OrderStats stats = clientService.getOrderStatsByPhone(phone);

        String customerName = null;
        if (!orderListPage.getContent().isEmpty()) {
            customerName = orderListPage.getContent().get(0).getCustomerName();
        }

        model.addAttribute("orderList", orderListPage.getContent());
        model.addAttribute("orderPage", orderListPage);
        model.addAttribute("phone", phone);
        model.addAttribute("customerName", customerName);
        model.addAttribute("stats", stats);
        model.addAttribute("hasOrders", !orderListPage.getContent().isEmpty());
        model.addAttribute("queryString", queryString);
        model.addAttribute("additionalCss", Arrays.asList("/css/client.css", "/css/list.css", "/css/pageLink.css"));
        return "client/order-list";
    }

    // http://localhost:8080/client/{phone}/orders/{orderId}
    @GetMapping("/client/{phone}/orders/{orderId}")
    public String detail(
            @PathVariable String phone,
            @PathVariable Long orderId,
            Model model) {
        OrderResponse.DetailDTO order = clientService.getDetailByPhoneAndOrderId(phone, orderId);
        model.addAttribute("order", order);
        model.addAttribute("phone", phone);
        model.addAttribute("additionalCss", Arrays.asList("/css/client.css", "/css/detail.css", "/css/order.css"));
        return "client/order-detail";
    }
}
