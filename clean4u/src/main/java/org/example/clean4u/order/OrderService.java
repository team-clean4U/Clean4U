package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.errors.exception.Exception500;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u._core.utils.FileUtil;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.customer.Grade;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.example.clean4u.laundryItem.LaundryItem;
import org.example.clean4u.laundryItem.LaundryItemRepository;
import org.example.clean4u.laundryOption.LaundryOption;
import org.example.clean4u.laundryOption.LaundryOptionRepository;
import org.example.clean4u.orderItemOption.OrderItemOption;
import org.example.clean4u.orderItemOption.OrderItemOptionRepository;
import org.example.clean4u.orderItemOption.OrderItemOptionResponse;
import org.example.clean4u.orderItem.*;
import org.example.clean4u.orderStatusHistory.OrderStatusHistory;
import org.example.clean4u.orderStatusHistory.OrderStatusHistoryRepository;
import org.example.clean4u.orderStatusHistory.OrderStatusHistoryResponse;
import org.example.clean4u.payment.Payment;
import org.example.clean4u.payment.PaymentRepository;
import org.example.clean4u.payment.PaymentStatus;
import org.example.clean4u.review.ReviewRepository;
import org.example.clean4u.review.ReviewResponse;
import org.example.clean4u.review.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final PaymentRepository paymentRepository;
    private final LaundryItemRepository laundryItemRepository;
    private final LaundryOptionRepository laundryOptionRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    // 주문 생성
    @Transactional
    public Order saveProc(OrderRequest.@Valid SaveDTO saveDto, Long sessionUserId) {
        Employee sessionUser = employeeRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        Customer customer = customerRepository.findById(saveDto.getCustomerId())
                .orElseThrow(() -> new Exception404("해당 고객을 찾을 수 없습니다."));

        String laundryImageFileName = null;

        if(saveDto.getLaundryImage() != null && !saveDto.getLaundryImage().isEmpty()) {
            try {
                if(!FileUtil.isImageFile(saveDto.getLaundryImage())) {
                    throw new Exception400("이미지 파일만 업로드 가능합니다.");
                }
                laundryImageFileName = FileUtil.saveFile(saveDto.getLaundryImage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        int totalPrice = calculateTotalPrice(saveDto.getItems());
        Order order = saveDto.toEntity(customer, totalPrice, sessionUser, laundryImageFileName, true);
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

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(order.getStatus())
                .editor(sessionUser)
                .build();
        orderStatusHistoryRepository.save(history);

        return orderRepository.save(order);
    }

    // 주문 검색 조회
    public PageResponse<OrderResponse.ListDTO> orderList(
            int page,
            int size,
            OrderRequest.SearchDTO searchDTO,
            Long sessionUserId) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if (!existingUser) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        LocalDate fromDate = searchDTO.getFromDate();
        LocalDate toDate = searchDTO.getToDate();

        if ((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
            throw new Exception400("검색 시작 날짜와 종료 날짜는 함께 입력해야 합니다.");
        }

        if (fromDate != null && fromDate.isAfter(toDate)) {
            throw new Exception400("검색 시작 날짜는 검색 종료 날짜보다 우선이어야 합니다.");
        }

        Page<Order> orderPage = orderRepository.searchOrders(pageable, searchDTO);

        return new PageResponse<>(orderPage, OrderResponse.ListDTO::new);
    }

    // 주문 상세 조회
    public OrderResponse.DetailDTO detail(Long orderId, Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if (!existingUser) {
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
        order.setTotalPrice(totalPrice);

        List<OrderStatusHistory> histories = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);

        List<OrderStatusHistoryResponse.DetailDTO> historyList = histories.stream()
                .map(OrderStatusHistoryResponse.DetailDTO::from)
                .toList();

        ReviewResponse.DetailDTO review = null;
        if (order.getReviewToken() != null && reviewRepository.existsByOrderId(order.getId())) {
            review = reviewService.getDetailByOrderId(order.getId());
        }

        return new OrderResponse.DetailDTO(order, itemDtos, historyList, review);
    }

    // 주문 변경 화면 요청
    public OrderResponse.UpdateFormDTO updateForm(Long orderId, Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if (!existingUser) {
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

        for (int i = 0; i < items.size(); i++) {
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

        return new OrderResponse.UpdateFormDTO(order, itemDtos);
    }

    // 주문 변경 기능 요청
    @Transactional
    public boolean updateProc(Long orderId, OrderRequest.@Valid UpdateDTO updateDto, Long sessionUserId) {
        Employee editor = employeeRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        OrderStatus previousStatus = order.getStatus();
        if(previousStatus == OrderStatus.CANCELLED) {
            throw new Exception400("접수 취소된 주문은 변경 불가능합니다.");
        }

        int totalPrice = calculateTotalPrice(updateDto.getItems());

        String oldLaundryImage = order.getLaundryImage();

        if(updateDto.getLaundryImage() != null && !updateDto.getLaundryImage().isEmpty()) {
            if(!FileUtil.isImageFile(updateDto.getLaundryImage())) {
                throw new Exception400("이미지 파일만 업로드 가능합니다.");
            }
            try {
                String newLaundryImageName = FileUtil.saveFile(updateDto.getLaundryImage());
                updateDto.setLaundryImageFileName(newLaundryImageName);

                if(oldLaundryImage != null && !oldLaundryImage.isEmpty()) {
                    FileUtil.deleteFile(oldLaundryImage);
                }
            } catch (IOException e) {
                throw new Exception500("파일 저장에 실패했습니다.");
            }
        } else {
            updateDto.setLaundryImageFileName(oldLaundryImage);
        }

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

        if(previousStatus != newStatus) {
            if(!previousStatus.canChangeTo(newStatus, order)) {
                throw new Exception400("이전 상태로 변경 혹은 결제 미완료 주문은 다음 단계로 변경이 불가능합니다.");
            }
            OrderStatusHistory history = OrderStatusHistory.builder()
                    .order(order)
                    .status(newStatus)
                    .editor(editor)
                    .build();
            orderStatusHistoryRepository.save(history);
        }

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

            reviewService.generateReviewToken(order.getId());
        }
        return beforGrade != customer.getGrade();
    }

    // 세탁물 사진 이미지 삭제
    @Transactional
    public void deleteLaundryImage(Long orderId, Long sessionUserId) {
        boolean existingUser = employeeRepository.existsById(sessionUserId);
        if (!existingUser) {
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        String laundryImage = order.getLaundryImage();
        if(laundryImage != null && !laundryImage.isEmpty()) {
            try {
                FileUtil.deleteFile(laundryImage);
            } catch (IOException e) {
                throw new Exception500("파일 삭제에 실패했습니다.");
            }
        }
        order.setLaundryImage(null);
    }

    // 취소 상태로 변경
    @Transactional
    public void updateStatus(Long orderId, Long sessionUserId) {
        Employee employee = employeeRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new Exception404("해당 결제정보를 찾을 수 없습니다."));

        if(order.getStatus() == OrderStatus.CANCELLED) {
            throw new Exception400("이미 취소된 주문입니다.");
        }

        if(order.getStatus() != OrderStatus.RECEIVED) {
            throw new Exception400("진행중인 주문은 취소할 수 없습니다.");
        }

        if(payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new Exception400("이미 결제된 주문은 취소할 수 없습니다.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                        .order(order)
                        .status(order.getStatus())
                        .editor(employee)
                         .build());
    }

    // 주문 삭제 기능 요청
    @Transactional
    public boolean deleteByOrderId(Long orderId, Long sessionUserId, boolean hardDelete) {
        Employee employee = employeeRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("해당 주문을 찾을 수 없습니다."));

        if(!hardDelete) {
            updateStatus(orderId, sessionUserId);
            return true;
        }
        if(!employee.isAdmin()) {
            throw new Exception403("삭제 권한이 없습니다.");
        }
        if(order.getStatus() != OrderStatus.CANCELLED) {
            throw new Exception400("취소된 주문만 삭제할 수 있습니다.");
        }
        if(!order.isPending()) {
            throw new Exception400("결제 대기 상태인 주문만 삭제 가능합니다.");
        }

        reviewRepository.findByOrderId(orderId)
                        .ifPresent(reviewRepository::delete);

        orderStatusHistoryRepository.deleteByOrderId(orderId);
        orderItemOptionRepository.deleteByOrderId(orderId);
        orderItemRepository.deleteByOrderId(orderId);
        orderRepository.deleteById(orderId);

        return orderRepository.existsById(orderId);
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

    // 주문 고유 번호 생성
    private String generateMerchantUid(Long orderId) {
        return "order_" + orderId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

}
