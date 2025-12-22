package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.customer.Grade;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    // 주문 생성
    @Transactional
    public Order saveProc(OrderRequest.@Valid SaveDto saveDto, Long sessionUserId) {
        Employee sessionUser = employeeRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        Customer customer = customerRepository.findById(saveDto.getCustomerId())
                .orElseThrow(() -> new Exception404("해당 고객을 찾을 수 없습니다."));

        int totalPrice = calculateTotalPrice(saveDto.getItems());
        Order order = saveDto.toEntity(customer, totalPrice, sessionUser);
        orderRepository.save(order);

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
            LocalDate toDate,
            Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if(!existingUser) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        if ((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
            throw new Exception400("검색 시작 날짜와 종료 날짜는 함께 입력해야 합니다.");
        }

        if (fromDate != null && fromDate.isAfter(toDate)) {
            throw new Exception400("검색 시작 날짜는 검색 종료 날짜보다 우선이어야 합니다.");
        }

        List<Order> orderList = orderRepository.searchOrder(status, customerName, phone, fromDate, toDate);
        return orderList.stream()
                .map(OrderResponse.ListDto::new)
                .toList();
    }

    // 주문 상세 조회
    public OrderResponse.DetailDto detail(Long orderId, Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if(!existingUser) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }
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

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);

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

            OrderItemResponse.DetailDto itemDto = new OrderItemResponse.DetailDto(item, optionDtos);

            itemDtos.add(itemDto);
        }
        return new OrderResponse.DetailDto(order, itemDtos, totalPrice);
    }

    // 주문 변경 화면 요청
    public OrderResponse.UpdateFormDto updateForm(Long orderId, Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if(!existingUser) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            throw new Exception404("주문 상세 내역을 찾을 수 없습니다.");
        }

        List<LaundryOption> allOptions = laundryOptionRepository.findAll();

        List<OrderItemResponse.UpdateFormDto> itemDtos = new ArrayList<>();

        for(int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);

            List<Long> selectedOptionIds = orderItemOptionRepository.findByOrderItemId(item.getId()).stream()
                    .map(o -> o.getLaundryOption().getId())
                    .toList();

            List<OrderItemResponse.UpdateFormOptionDto> optionDtos = allOptions.stream()
                    .map(option -> {
                        OrderItemResponse.UpdateFormOptionDto dto =
                                new OrderItemResponse.UpdateFormOptionDto(
                                        option, selectedOptionIds.contains(option.getId()));
                        return dto;
                    })
                    .toList();

            OrderItemResponse.UpdateFormDto dto = new OrderItemResponse.UpdateFormDto(i, item, optionDtos);
            itemDtos.add(dto);
        }

        return new OrderResponse.UpdateFormDto(order, itemDtos);
    }

    // 주문 변경 기능 요청
    @Transactional
    public boolean updateProc(Long orderId, OrderRequest.@Valid UpdateDto updateDto, Long sessionUserId) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        int totalPrice = calculateTotalPrice(updateDto.getItems());

        Employee editor = employeeRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        // 2. 주문 기본 정보 수정
        order.updateOrder(updateDto);
        order.updatePrice(totalPrice);
        order.updateEditor(editor);

        entityManager.flush();

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

        OrderStatus newStatus = updateDto.getStatus();

        Customer customer = customerRepository.findById(order.getCustomer().getId())
                .orElseThrow(() -> new Exception404("해당 고객을 찾을 수 없습니다."));

        Grade beforGrade = customer.getGrade();

        if (newStatus == OrderStatus.COMPLETED) {
            Long completedCount = orderRepository.countByCustomerIdAndStatus(customer.getId(), OrderStatus.COMPLETED);

            if (completedCount >= 30) {
                customer.setGrade(Grade.VIP);
            } else if (completedCount >= 5) {
                customer.setGrade(Grade.REGULAR);
            }
        }

        return beforGrade != customer.getGrade();
    }

    // 주문 삭제 기능 요청
    @Transactional
    public void deleteByOrderId(Long orderId, Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if(!existingUser) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        orderItemOptionRepository.deleteByOrderId(orderId);
        orderItemRepository.deleteByOrderId(orderId);
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
