package org.example.clean4u.order;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception401;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.laundryItem.LaundryItemRepository;
import org.example.clean4u.laundryOption.LaundryOption;
import org.example.clean4u.laundryOption.LaundryOptionRepository;
import org.example.clean4u.order.orderItem.OrderItem;
import org.example.clean4u.order.orderItem.OrderItemDto;
import org.example.clean4u.order.orderItem.OrderItemRepository;
import org.example.clean4u.order.orderItem.OrderItemRequest;
import org.example.clean4u.order.orderItemOption.OrderItemOption;
import org.example.clean4u.order.orderItemOption.OrderItemOptionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final LaundryItemRepository laundryItemRepository;
    private final LaundryOptionRepository laundryOptionRepository;

    // 주문 생성 화면 요청 - http://localhost:8080/order
    // 인증(o), 인가(x)
    @GetMapping("/order")
    public String saveForm(HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        return "order/save-form";
    }

    // 주문 생성 기능
    // 인증(o), 인가(x)
    @PostMapping("/order")
    public String saveProc(OrderRequest.SaveDto saveDto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }

        Customer customer = customerRepository.findById(saveDto.getCustomerId());
        if (customer == null) {
            throw new Exception404("해당 고객을 찾을 수 없습니다.");
        }

        Long totalPrice = calculateTotalPrice(saveDto.getItems());

        Order order = saveDto.toEntity(customer, totalPrice, sessionUser);

        for (OrderItemRequest.SaveDto item : saveDto.getItems()) {
            LaundryItem laundryItem = laundryItemRepository.findById(item.getLaundryItemId());
            OrderItem orderItem = orderItemRepository.save(item.toEntity(order, laundryItem));

            if (item.getOptionIds() != null) {
                for (Long optionId : item.getOptionIds()) {
                    LaundryOption option = laundryOptionRepository.findById(optionId);
                    OrderItemOption orderItemOption = OrderItemOption.builder()
                            .laundryOption(option)
                            .orderItem(orderItem)
                            .build();
                    orderItemOptionRepository.save(orderItemOption);
                }
            }

        }
        orderRepository.save(order);

        return "redirect:/order/" + order.getId();
    }

    // 주문 목록 조회 화면 요청: http://localhost:8080/order/list
    // 인증(o), 인가(x)
    @GetMapping("/order/list")
    public String orderList(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }

        List<Order> orderList = orderRepository.findAll();
        model.addAttribute("orderList", orderList);
        return "order/list-form";
    }

    // 주문 상세 조회 화면 요청: http://localhost:8080/order/{id}
    // 인증(o), 인가(x)
    @GetMapping("/order/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }

        Order order = orderRepository.findById(id);
        model.addAttribute("order", order);
        return "order/detail-form";
    }

    // 주문 변경 화면 요청: http://localhost:8080/order/{id}/update
    // 인증(o), 인가(x)
    @GetMapping("/order/{id}/update")
    public String updateForm(@PathVariable Long id, HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        Order order = orderRepository.findById(id);
        model.addAttribute("order", order);
        return "order/update-form";
    }

    // 주문 변경 기능 요청: http://localhost:8080/order/{id}/update
    // 인증(o), 인가(x)
    @PostMapping("/order/{id}/update")
    public String updateProc(@PathVariable Long id, HttpSession session, OrderRequest.UpdateDto updateDto) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }

        // 1. 주문 조회
        Order order = orderRepository.findById(id);

        Long totalPrice = calculateTotalPrice(updateDto.getItems());

        // 2. 주문 기본 정보 수정(더티 체크)
        order.updateOrder(updateDto, totalPrice, sessionUser);

        // 3. 기존 주문 옵션 모두 삭제
        orderItemOptionRepository.deleteByOrderId(order.getId());

        // 4. 기존 주문 세부 품목 모두 삭제
        orderItemRepository.deleteByOrderId(order.getId());

        // 5. 새로운 주문 품목 생성
        for (OrderItemRequest.UpdateDto item : updateDto.getItems()) {
            LaundryItem laundryItem = laundryItemRepository.findById(item.getLaundryItemId());
            OrderItem orderItem = item.toEntity(order, laundryItem);
            orderItemRepository.save(orderItem);

            List<Long> options = item.getOptionIds();

            if (options != null) {
                for (Long optionId : options) {
                    LaundryOption laundryOption = laundryOptionRepository.findById(optionId);
                    OrderItemOption orderItemOption = OrderItemOption.builder()
                            .laundryOption(laundryOption)
                            .orderItem(orderItem)
                            .build();
                    orderItemOptionRepository.save(orderItemOption);
                }
            }
        }
        return "redirect:/order/" + order.getId();
    }

    // 주문 처리 상태 변경 기능 요청: http://localhost:8080/order/{id}/status-update
    // 인증(o), 인가(x)
    @PostMapping("/order/{id}/status-update")
    public String updateStatusProc(@PathVariable Long id, HttpSession session, OrderStatus newStatus) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        Order order = orderRepository.findById(id);
        order.updateStatus(newStatus);

        return "redirect:/order/" + order.getId();
    }

    // 주문 삭제: http://localhost:8080/order/{id}
    // 인증(o), 인가(x)
    @PostMapping("/order/{id}")
    public String deleteOrder(@PathVariable Long id, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        orderRepository.deleteById(id);
        return "redirect:/order/list";
    }

    public Long calculateTotalPrice(List<? extends OrderItemDto> items) {
        long totalPrice = 0L;

        for(OrderItemDto item: items) {
            LaundryItem laundryItem = laundryItemRepository.findById(item.getLaundryItemId());

            long itemPrice = (long) laundryItem.getBasePrice() * item.getQuantity();

            if(item.getOptionIds() != null) {
                for(Long optionId: item.getOptionIds()) {
                    LaundryOption option = laundryOptionRepository.findById(optionId);
                    itemPrice += option.getExtraPrice();
                }
            }
            totalPrice += itemPrice;
        }
        return totalPrice;
    }

}
