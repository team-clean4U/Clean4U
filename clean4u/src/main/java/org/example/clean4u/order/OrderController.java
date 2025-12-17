package org.example.clean4u.order;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성 화면 요청 - http://localhost:8080/order/save
    // 인증(o), 인가(x)
    @GetMapping("/order/save")
    public String saveForm() {
        return "order/save-form";
    }

    // 주문 생성 기능
    // 인증(o), 인가(x)
    @PostMapping("/order/save")
    public String saveProc(OrderRequest.@Valid SaveDto saveDto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        Order order = orderService.saveProc(saveDto, sessionUser.getId());
        return "redirect:/order/" + order.getId();
    }

    // 주문 목록 조회, 검색 화면: http://localhost:8080/order/list
    // 인증(o), 인가(x)
    @GetMapping("/order/list")
    public String orderList(
            Model model,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            HttpSession session
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        List<OrderResponse.ListDto> orderList = orderService.orderList(status, customerName, phone, fromDate, toDate, sessionUser.getId());

        model.addAttribute("orderList", orderList);
        return "order/list-form";
    }

    // 주문 상세 조회 화면
    // 인증(o), 인가(x)
    @GetMapping("/order/{orderId}")
    public String detail(@PathVariable Long orderId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        OrderResponse.DetailDto dto = orderService.detail(orderId, sessionUser.getId());
        model.addAttribute("order", dto);
        model.addAttribute("items", dto.getItems());
        return "order/detail-form";
    }

    // 주문 변경 기능 요청: http://localhost:8080/order/{id}/update
    // 인증(o), 인가(x)
    @PostMapping("/order/{orderId}/update")
    public String updateProc(@PathVariable Long orderId, OrderRequest.@Valid UpdateDto updateDto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.updateProc(orderId, updateDto, sessionUser.getId());
        return "redirect:/order/" + orderId;
    }

    // 주문 처리 상태 변경 기능 요청: http://localhost:8080/order/{id}/status-update
    // 인증(o), 인가(x)
    @PostMapping("/order/{orderId}/status-update")
    public String updateStatusProc(@PathVariable Long orderId, OrderStatus newStatus, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.updateStatusProc(orderId, newStatus, sessionUser.getId());
        return "redirect:/order/" + orderId;
    }

    // 주문 삭제: http://localhost:8080/order/{id}
    // 인증(o), 인가(x)
    @PostMapping("/order/{orderId}")
    public String deleteByOrderId(@PathVariable Long orderId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.deleteByOrderId(orderId, sessionUser.getId());
        return "redirect:/order/list";
    }

}
