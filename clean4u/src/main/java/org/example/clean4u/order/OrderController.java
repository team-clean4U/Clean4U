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
import org.example.clean4u.payment.Payment;
import org.example.clean4u.payment.PaymentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LaundryItemService laundryItemService;
    private final LaundryOptionService laundryOptionService;
    private final CustomerService customerService;
    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    @GetMapping("/orders/new")
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
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/order.css"));

        return "order/save-form";
    }

    @PostMapping("/orders/new")
    public String saveProc(@Valid OrderRequest.SaveDTO saveDTO,  HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        Order order = orderService.saveProc(saveDTO, sessionUser.getId());

        return "redirect:/orders/" + order.getId();
    }

    @GetMapping("/orders")
    public String orderList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @ModelAttribute OrderRequest.SearchDTO searchDTO,
            HttpServletRequest request
    ) {
        int pageIndex = Math.max(0, page - 1);

        String queryString = request.getQueryString();
        if(queryString != null) {
            queryString = queryString.replaceAll("(page=\\d+)", "");
            queryString = queryString.replaceAll("(&size=\\d+)", "");
            if(!queryString.isBlank()) {
                queryString = "&" + queryString;
            }
        }
        PageResponse<OrderResponse.ListDTO> orderListPage = orderService.orderList(pageIndex, size, searchDTO);
        model.addAttribute("orderList", orderListPage.getContent());
        model.addAttribute("orderPage", orderListPage);
        model.addAttribute("searchView", searchDTO);
        model.addAttribute("queryString", queryString);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/order.css", "/css/customer.css"));

        return "order/list-form";
    }

    @GetMapping("/orders/{orderId}")
    public String detail(@PathVariable Long orderId, Model model) {
        OrderResponse.DetailDTO order = orderService.detail(orderId);
        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("items", order.getItems());
        model.addAttribute("history", order.getHistories());
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/order.css", "/css/payment.css", "/css/customer.css"));
        return "order/detail-form";
    }

    @GetMapping("/orders/{orderId}/edit")
    public String updateForm(@PathVariable Long orderId, Model model) {
        OrderResponse.UpdateFormDTO order = orderService.updateForm(orderId);
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
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/detail.css", "/css/order.css", "/css/customer.css"));

        return "order/update-form";
    }

    @PostMapping("/orders/{orderId}/review-link/send")
    public String sendReviewLink(@PathVariable Long orderId, HttpSession session, RedirectAttributes redirectAttributes) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        try {
            orderService.sendReviewLinkSms(orderId, sessionUser.getId());
            redirectAttributes.addFlashAttribute("alertMessage", "리뷰 작성 링크가 고객에게 발송되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alertMessage", "리뷰 링크 발송에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/orders/" + orderId;
    }
}