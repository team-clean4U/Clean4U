package org.example.clean4u.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.customer.CustomerService;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.laundryItem.LaundryItemResponse;
import org.example.clean4u.laundryItem.LaundryItemService;
import org.example.clean4u.laundryOption.LaundryOptionService;
import org.example.clean4u.orderItem.OrderItemResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String saveProc(OrderRequest.@Valid SaveDTO saveDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        Order order = orderService.saveProc(saveDTO, sessionUser.getId());

        return "redirect:/order/" + order.getId();
    }

    @GetMapping("/order/list")
    public String orderList(
            Model model,
            // ToDo: DTO 로 묶기
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            HttpSession session,
            HttpServletRequest request
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        int pageIndex = Math.max(0, page - 1);

        String queryString = request.getQueryString();

        if(queryString != null) {
            queryString = queryString.replaceAll("(&page=\\d+)", "");
            queryString = queryString.replaceAll("(&size=\\d+)", "");
            if(queryString.isBlank()) {
                queryString = "&" + queryString;
            }
        }
        PageResponse<OrderResponse.ListDTO> orderListPage = orderService.orderList(pageIndex, size, status, customerName, phone, fromDate, toDate, sessionUser.getId());
        model.addAttribute("orderList", orderListPage.getContent());
        model.addAttribute("orderPage", orderListPage);
        model.addAttribute("customerName", customerName);
        model.addAttribute("phone", phone);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("queryString", queryString);

        return "order/list-form";
    }

    @GetMapping("/order/{orderId}")
    public String detail(@PathVariable Long orderId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        OrderResponse.DetailDTO order = orderService.detail(orderId, sessionUser.getId());
        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());
        return "order/detail-form";
    }

    @GetMapping("/order/{orderId}/update")
    public String updateForm(@PathVariable Long orderId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        OrderResponse.UpdateFormDTO order = orderService.updateForm(orderId, sessionUser.getId());
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
    public String updateProc(@PathVariable Long orderId, OrderRequest.@Valid UpdateDTO updateDto, HttpSession session, RedirectAttributes redirectAttributes) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        boolean isGradeChanged = orderService.updateProc(orderId, updateDto, sessionUser.getId());
        if (isGradeChanged) {
            redirectAttributes.addFlashAttribute("alertMessage", "고객 등급이 변경되었습니다.");
        }
        return "redirect:/order/" + orderId;
    }

    @PostMapping("/order/{orderId}/delete")
    public String deleteByOrderId(@PathVariable Long orderId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.deleteByOrderId(orderId, sessionUser.getId());
        return "redirect:/order/list";
    }

}
