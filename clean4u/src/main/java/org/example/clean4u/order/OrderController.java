package org.example.clean4u.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception400;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.laundryItem.LaundryItemResponse;
import org.example.clean4u.laundryItem.LaundryItemService;
import org.example.clean4u.laundryOption.LaundryOptionService;
import org.example.clean4u.order.orderItem.OrderItemRequest;
import org.example.clean4u.order.orderItem.OrderItemResponse;
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
    private final LaundryItemService laundryItemService;
    private final LaundryOptionService laundryOptionService;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

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

        if((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
            throw new Exception400("검색 시작 날짜와 종료 날짜는 함께 입력해야 합니다.");
        }

        if(fromDate != null && fromDate.isAfter(toDate)) {
            throw new Exception400("검색 시작 날짜는 검색 종료 날짜보다 우선이어야 합니다.");
        }

        List<OrderResponse.ListDto> orderList = orderService.orderList(status, customerName, phone, fromDate, toDate, sessionUser.getId());
        model.addAttribute("orderList", orderList);
        return "order/list-form";
    }

    // 주문 상세 조회 화면
    // 인증(o), 인가(x)
    @GetMapping("/order/{orderId}")
    public String detail(@PathVariable Long orderId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        OrderResponse.DetailDto order = orderService.detail(orderId, sessionUser.getId());
        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());
        return "order/detail-form";
    }

    // 주문 변경 화면 요청
    @GetMapping("/order/{orderId}/update")
    public String updateForm(@PathVariable Long orderId, Model model, HttpSession session) throws JsonProcessingException {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        OrderResponse.UpdateFormDto order = orderService.updateForm(orderId, sessionUser.getId());
        List<LaundryItemResponse.ListDTO> laundryItems = laundryItemService.getAllLaundryItems();

        List<OrderItemResponse.UpdateFormDto> items = order.getItems();
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("itemsSize", items != null ? items.size() : 0);
        model.addAttribute("laundryItems", laundryItems);

        try {
            String laundryItemJson = objectMapper.writeValueAsString(laundryItemService.getAllLaundryItems());
            String laundryOptionsJson = objectMapper.writeValueAsString(laundryOptionService.getAllLaundryOptions());
            model.addAttribute("laundryItemJson", laundryItemJson);
            model.addAttribute("laundryOptionsJson", laundryOptionsJson);
        } catch (Exception e) {
            model.addAttribute("laundryItemJson", "[]");
            model.addAttribute("laundryOptionsJson", "[]");
        }

        return "order/update-form";
    }

    // 주문 변경 기능 요청: http://localhost:8080/order/{id}/update
    // 인증(o), 인가(x)
    @PostMapping("/order/{orderId}/update")
    public String updateProc(@PathVariable Long orderId, OrderRequest.@Valid UpdateDto updateDto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        Order order = orderService.updateProc(orderId, updateDto, sessionUser.getId());
        return "redirect:/order/" + order.getId();
    }

    // 주문 삭제: http://localhost:8080/order/{id}
    // 인증(o), 인가(x)
    @PostMapping("/order/{orderId}/delete")
    public String deleteByOrderId(@PathVariable Long orderId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.deleteByOrderId(orderId, sessionUser.getId());
        return "redirect:/order/list";
    }

}
