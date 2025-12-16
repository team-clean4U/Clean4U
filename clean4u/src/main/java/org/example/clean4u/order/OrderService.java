package org.example.clean4u.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.customer.Grade;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.laundryItem.LaundryItemRepository;
import org.example.clean4u.laundryOption.LaundryOption;
import org.example.clean4u.laundryOption.LaundryOptionRepository;
import org.example.clean4u.order.orderItem.*;
import org.example.clean4u.order.orderItemOption.OrderItemOption;
import org.example.clean4u.order.orderItemOption.OrderItemOptionRepository;
import org.example.clean4u.order.orderItemOption.OrderItemOptionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final LaundryItemRepository laundryItemRepository;
    private final LaundryOptionRepository laundryOptionRepository;

    // 주문 생성
    @Transactional
    public Order save(OrderRequest.@Valid SaveDto saveDto, Employee sessionUser) {
        Customer customer = customerRepository.findById(saveDto.getCustomerId())
                .orElseThrow(() -> new Exception404("해당 고객을 찾을 수 없습니다."));

        int totalPrice = calculateTotalPrice(saveDto.getItems());
        Order order = saveDto.toEntity(customer, totalPrice, sessionUser);

        for (OrderItemRequest.SaveDto item : saveDto.getItems()) {
            LaundryItem laundryItem = laundryItemRepository.findById(item.getLaundryItemId())
                    .orElseThrow(() -> new Exception404("해당 세탁 품목을 찾을 수 없습니다."));
            OrderItem orderItem = orderItemRepository.save(item.toEntity(order, laundryItem));

            if (item.getOptionIds() != null) {
                for (Long optionId : item.getOptionIds()) {
                    LaundryOption option = laundryOptionRepository.findById(optionId)
                            .orElseThrow(() -> new Exception404("해당 세탁 옵션을 찾을 수 없습니다."));
                    OrderItemOption orderItemOption = OrderItemOption.builder()
                            .laundryOption(option)
                            .orderItem(orderItem)
                            .build();
                    orderItemOptionRepository.save(orderItemOption);
                }
            }
        }
        return orderRepository.save(order);
    }

    // 주문 검색 조회
    public List<OrderResponse.ListDto> orderList(
            OrderStatus status,
            String customerName,
            String phone,
            LocalDate fromDate,
            LocalDate toDate) {
        List<Order> orderList = orderRepository.searchOrder(status, customerName, phone, fromDate, toDate);
        return orderList.stream()
                .map(OrderResponse.ListDto::new)
                .toList();
    }

    // 주문 상세 조회
    public OrderResponse.DetailDto detailDto(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        if (orderItems == null || orderItems.isEmpty()) {
            throw new Exception404("주문 상세 내역을 찾을 수 없습니다.");
        }

        List<OrderItemOption> allOptions = orderItemOptionRepository.findAllByOrderId(orderId);

        Map<Long, List<OrderItemOption>> optionMap = allOptions.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderItem().getId()
                ));

        int totalPrice = 0; // 하나의 Order 의 총 가격
        List<OrderItemResponse.DetailDto> itemDtos = new ArrayList<>();

        for (OrderItem item : orderItems) {
            int basePrice = item.getLaundryItem().getBasePrice();
            int quantity = item.getQuantity();

            List<OrderItemOption> options = optionMap.getOrDefault(item.getId(), List.of());

            // 총 순수 옵션 금액
            int optionTotal = options.stream()
                    .mapToInt(o -> o.getLaundryOption().getExtraPrice())
                    .sum();

            // 하나의 주문 아이템 금액(옵션 포함)
            int itemTotal = basePrice * quantity + optionTotal;

            // 하나의 주문 총 금액(여러 주문 아이템의 합)
            totalPrice += itemTotal;

            List<OrderItemOptionResponse.DetailDto> optionDtos = options.stream()
                    .map(o -> {
                        OrderItemOptionResponse.DetailDto dto = new OrderItemOptionResponse.DetailDto(o);
                        return dto;
                    }).toList();

            OrderItemResponse.DetailDto itemDto = new OrderItemResponse.DetailDto(
                    item, optionDtos);

            itemDtos.add(itemDto);
        }
        return new OrderResponse.DetailDto(order, itemDtos);
    }

    // 주문 변경 화면 요청
    public OrderResponse.UpdateFormDto updateForm(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            throw new Exception404("주문 상세 내역을 찾을 수 없습니다.");
        }

        List<OrderItemResponse.DetailDto> itemDtos = items.stream()
                .map(oi -> {
                    List<OrderItemOption> options = orderItemOptionRepository.findByOrderItemId(oi.getId());
                    if (options == null) {
                        throw new Exception404("해당 옵션을 찾을 수 없습니다.");
                    }
                    List<OrderItemOptionResponse.DetailDto> optionDtos = options.stream()
                            .map(OrderItemOptionResponse.DetailDto::new)
                            .toList();

                    return new OrderItemResponse.DetailDto(oi, optionDtos);
                })
                .toList();

        return new OrderResponse.UpdateFormDto(order, itemDtos);
    }

    // 주문 변경 기능 요청
    @Transactional
    public Order updateProc(Long orderId, OrderRequest.@Valid UpdateDto updateDto, Employee editor) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        int totalPrice = calculateTotalPrice(updateDto.getItems());

        // 2. 주문 기본 정보 수정
        order.updateOrder(updateDto);
        order.updatePrice(totalPrice);
        order.updateEditor(editor);

        // 3. 기존 주문 옵션 모두 삭제
        orderItemOptionRepository.deleteByOrderId(order.getId());

        // 4. 기존 주문 세부 품목 삭제
        orderItemRepository.deleteByOrderId(order.getId());

        // 5. 새로운 주문 품목 생성
        for (OrderItemRequest.UpdateDto item : updateDto.getItems()) {
            LaundryItem laundryItem = laundryItemRepository.findById(item.getLaundryItemId())
                    .orElseThrow(() -> new Exception404("해당 세탁 품목을 찾을 수 없습니다."));

            OrderItem orderItem = item.toEntity(order, laundryItem);
            orderItemRepository.save(orderItem);

            List<Long> options = item.getOptionIds();

            if (options != null) {
                for (Long optionId : options) {
                    LaundryOption laundryOption = laundryOptionRepository.findById(optionId)
                            .orElseThrow(() -> new Exception404("해당 세탁 옵션을 찾을 수 없습니다."));

                    OrderItemOption orderItemOption = OrderItemOption.builder()
                            .laundryOption(laundryOption)
                            .orderItem(orderItem)
                            .build();

                    orderItemOptionRepository.save(orderItemOption);
                }
            }
        }
        return order;
    }

    // 주문 처리 상태 변경 기능 요청
    @Transactional
    public Order updateStatusProc(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문이 없습니다."));
        order.updateStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED) {
            Customer customer = order.getCustomer();

            Long completedCount = orderRepository.countByCustomerAndStatus(customer, OrderStatus.COMPLETED);

            if (completedCount >= 30) {
                customer.setGrade(Grade.REGULAR);
            } else if (completedCount >= 5) {
                customer.setGrade(Grade.VIP);
            }
        }
        return order;
    }

    // 주문 삭제 기능 요청
    @Transactional
    public void deleteByOrderId(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    // 총 합 계산
    public int calculateTotalPrice(List<? extends OrderItemRequestDto> items) {
        int totalPrice = 0;

        for (OrderItemRequestDto item : items) {
            LaundryItem laundryItem = laundryItemRepository.findById(item.getLaundryItemId())
                    .orElseThrow(() -> new Exception404("해당 세탁 품목을 찾을 수 없습니다."));

            int itemPrice = laundryItem.getBasePrice() * item.getQuantity();

            if (item.getOptionIds() != null) {
                for (Long optionId : item.getOptionIds()) {
                    LaundryOption option = laundryOptionRepository.findById(optionId)
                            .orElseThrow(() -> new Exception404("해당 세탁 옵션을 찾을 수 없습니다."));
                    itemPrice += option.getExtraPrice();
                }
            }
            totalPrice += itemPrice;
        }
        return totalPrice;
    }

}
