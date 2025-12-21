package org.example.clean4u.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception400;
import org.example.clean4u.customer.CustomerService;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.laundryItem.LaundryItemResponse;
import org.example.clean4u.laundryItem.LaundryItemService;
import org.example.clean4u.laundryOption.LaundryOptionService;
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
    private final CustomerService customerService;
    private final ObjectMapper objectMapper;

    @GetMapping("/order/save")
    public String saveForm(Model model) {

        List<LaundryItemResponse.ListDTO> laundryItems = laundryItemService.getAllLaundryItems();
        model.addAttribute("laundryItems", laundryItems);

        try {
            String customerJson = objectMapper.writeValueAsString(customerService.getAllCustomers());
            String laundryItemJson = objectMapper.writeValueAsString(laundryItemService.getAllLaundryItems());
            String laundryOptionsJson = objectMapper.writeValueAsString(laundryOptionService.getAllLaundryOptions());
            model.addAttribute("customerJson", customerJson);
            model.addAttribute("laundryItemJson", laundryItemJson);
            model.addAttribute("laundryOptionsJson", laundryOptionsJson);
        } catch (Exception e) {
            model.addAttribute("customerJson", "[]");
            model.addAttribute("laundryItemJson", "[]");
            model.addAttribute("laundryOptionsJson", "[]");
        }

        return "order/save-form";
    }

    @PostMapping("/order/save")
    public String saveProc(OrderRequest.@Valid SaveDto saveDto, HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        Order order = orderService.saveProc(saveDto, sessionUser.getId());

        return "redirect:/order/" + order.getId();
    }

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
        model.addAttribute("customerName", customerName);
        model.addAttribute("phone", phone);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "order/list-form";
    }

    @GetMapping("/order/{orderId}")
    public String detail(@PathVariable Long orderId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        OrderResponse.DetailDto order = orderService.detail(orderId, sessionUser.getId());
        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());
        return "order/detail-form";
    }

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

    @PostMapping("/order/{orderId}/update")
    public String updateProc(@PathVariable Long orderId, OrderRequest.@Valid UpdateDto updateDto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        Order order = orderService.updateProc(orderId, updateDto, sessionUser.getId());
        return "redirect:/order/" + order.getId();
    }

    @PostMapping("/order/{orderId}/delete")
    public String deleteByOrderId(@PathVariable Long orderId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.deleteByOrderId(orderId, sessionUser.getId());
        return "redirect:/order/list";
    }

}
