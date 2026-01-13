package org.example.clean4u.client;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderResponse;
import org.example.clean4u.order.OrderStatus;
import org.example.clean4u.orderItem.OrderItem;
import org.example.clean4u.orderItem.OrderItemRepository;
import org.example.clean4u.orderItem.OrderItemResponse;
import org.example.clean4u.orderItemOption.OrderItemOption;
import org.example.clean4u.orderItemOption.OrderItemOptionRepository;
import org.example.clean4u.orderItemOption.OrderItemOptionResponse;
import org.example.clean4u.orderStatusHistory.OrderStatusHistory;
import org.example.clean4u.orderStatusHistory.OrderStatusHistoryRepository;
import org.example.clean4u.orderStatusHistory.OrderStatusHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private String validateAndFormatPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new Exception400("전화번호를 입력해주세요.");
        }
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (cleanPhone.isEmpty()) {
            throw new Exception400("전화번호에 숫자가 포함되어야 합니다.");
        }
        if (cleanPhone.length() == 11) {
            return cleanPhone.substring(0, 3) + "-" + cleanPhone.substring(3, 7) + "-" + cleanPhone.substring(7);
        } else if (cleanPhone.length() == 10) {
            return cleanPhone.substring(0, 3) + "-" + cleanPhone.substring(3, 6) + "-" + cleanPhone.substring(6);
        }
        throw new Exception400("전화번호 형식이 올바르지 않습니다.");
    }

    private Customer findCustomerByPhone(String phone) {
        String formattedPhone = validateAndFormatPhone(phone);
        return customerRepository.findByPhone(formattedPhone)
                .orElseThrow(() -> new Exception404("해당 전화번호로 등록된 고객을 찾을 수 없습니다."));
    }

    public OrderResponse.DetailDTO getDetailByPhoneAndOrderId(String phone, Long orderId) {
        Customer customer = findCustomerByPhone(phone);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }

        return getClientOrderDetail(order);
    }

    public PageResponse<OrderResponse.ListDTO> getOrderListByPhone(String phone, int page, int size) {
        Customer customer = findCustomerByPhone(phone);

        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<Order> orderPage;
        orderPage = orderRepository.findByCustomerIdOrderByIdDesc(customer.getId(), pageable);

        PageResponse<OrderResponse.ListDTO> orderListPage = new PageResponse<>(orderPage, OrderResponse.ListDTO::new);
        String formattedPhone = validateAndFormatPhone(phone);
        orderListPage.getContent().forEach(dto -> dto.setPhone(formattedPhone));

        return orderListPage;
    }

    private OrderResponse.DetailDTO getClientOrderDetail(Order order) {
        Long orderId = order.getId();
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        List<OrderItemOption> allOptions = orderItemOptionRepository.findAllByOrderId(orderId);

        Map<Long, List<OrderItemOption>> optionMap = allOptions.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderItem().getId()
                ));

        int totalPrice = 0;
        List<OrderItemResponse.DetailDto> itemDtos = new ArrayList<>();

        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {

                int basePrice = item.getLaundryItem().getBasePrice();
                int quantity = item.getQuantity();

                List<OrderItemOption> options = optionMap.getOrDefault(item.getId(), List.of());

                int optionTotal = options.stream()
                        .mapToInt(o -> o.getLaundryOption().getExtraPrice())
                        .sum();

                int itemTotal = basePrice * quantity + optionTotal;

                totalPrice += itemTotal;

                List<OrderItemOptionResponse.DetailDto> optionDtos = options.stream()
                        .map(o -> {
                            OrderItemOptionResponse.DetailDto dto = new OrderItemOptionResponse.DetailDto(o);
                            return dto;
                        }).toList();

                OrderItemResponse.DetailDto itemDto = new OrderItemResponse.DetailDto(item, optionDtos);

                itemDtos.add(itemDto);
            }
            order.setTotalPrice(totalPrice);
        }

        List<OrderStatusHistory> histories = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);

        List<OrderStatusHistoryResponse.DetailDTO> historyList = histories.stream()
                .map(OrderStatusHistoryResponse.DetailDTO::from)
                .toList();

        return new OrderResponse.DetailDTO(order, itemDtos, historyList, null);
    }

    public ClientResponse.OrderStats getOrderStatsByPhone(String phone) {
        Customer customer = findCustomerByPhone(phone);
        List<Order> orders = orderRepository.findByCustomerIdOrderByIdDesc(customer.getId());

        int totalOrders = orders.size();
        int totalAmount = orders.stream()
                .mapToInt(order -> order.getTotalPrice() != null ? order.getTotalPrice() : 0)
                .sum();
        int completedOrders = (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .count();
        int pendingOrders = (int) orders.stream()
                .filter(Order::isPending)
                .count();

        return new ClientResponse.OrderStats(totalOrders, totalAmount, completedOrders, pendingOrders);
    }
}
