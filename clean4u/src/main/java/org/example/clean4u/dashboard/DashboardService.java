package org.example.clean4u.dashboard;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.utils.PriceUtil;
import org.example.clean4u.laundryItem.LaundryCategory;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderStatus;
import org.example.clean4u.orderItem.OrderItem;
import org.example.clean4u.orderItem.OrderItemRepository;
import org.example.clean4u.orderItemOption.OrderItemOption;
import org.example.clean4u.orderItemOption.OrderItemOptionRepository;
import org.example.clean4u.supplyItem.SupplyItem;
import org.example.clean4u.supplyItem.SupplyItemRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final SupplyItemRepository supplyItemRepository;

    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 전체 주문 중 가장 많이 주문된 세탁 품목 카테고리
        List<OrderItem> allOrderItems = orderItemRepository.findAllWithLaundryItem();
        Map<LaundryCategory, Integer> categoryCountMap = new HashMap<>();
        Map<LaundryCategory, Long> categoryTotalPriceMap = new HashMap<>();
        Map<LaundryCategory, Integer> categoryMaxPriceMap = new HashMap<>();
        Map<LaundryCategory, Integer> categoryMinPriceMap = new HashMap<>();

        for (OrderItem orderItem : allOrderItems) {
            LaundryCategory category = orderItem.getLaundryItem().getCategory();
            int quantity = orderItem.getQuantity() != null ? orderItem.getQuantity() : 0;
            int basePrice = orderItem.getLaundryItem().getBasePrice() != null ? orderItem.getLaundryItem().getBasePrice() : 0;

            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
            categoryTotalPriceMap.put(category, categoryTotalPriceMap.getOrDefault(category, 0L) + ((long) basePrice * quantity));

            if (!categoryMaxPriceMap.containsKey(category) || basePrice > categoryMaxPriceMap.get(category)) {
                categoryMaxPriceMap.put(category, basePrice);
            }
            if (!categoryMinPriceMap.containsKey(category) || basePrice < categoryMinPriceMap.get(category)) {
                categoryMinPriceMap.put(category, basePrice);
            }
        }

        LaundryCategory mostOrderedCategory = null;
        int maxCategoryCount = 0;
        int categoryMaxPrice = 0;
        int categoryMinPrice = 0;
        long categoryTotalPrice = 0;

        for (Map.Entry<LaundryCategory, Integer> entry : categoryCountMap.entrySet()) {
            if (entry.getValue() > maxCategoryCount) {
                maxCategoryCount = entry.getValue();
                mostOrderedCategory = entry.getKey();

                categoryTotalPrice = categoryTotalPriceMap.getOrDefault(entry.getKey(), 0L);
                categoryMaxPrice = categoryMaxPriceMap.getOrDefault(entry.getKey(), 0);
                categoryMinPrice = categoryMinPriceMap.getOrDefault(entry.getKey(), 0);
            }
        }

        statistics.put("mostOrderedCategoryIcon", mostOrderedCategory != null ? mostOrderedCategory.getIcon() : "box");
        statistics.put("mostOrderedCategory", mostOrderedCategory != null ? mostOrderedCategory.name() : "없음");
        statistics.put("mostOrderedCategoryCount", PriceUtil.format(maxCategoryCount));
        statistics.put("mostOrderedCategoryMaxPrice", PriceUtil.format(categoryMaxPrice));
        statistics.put("mostOrderedCategoryMinPrice", PriceUtil.format(categoryMinPrice));
        statistics.put("mostOrderedCategoryTotalPrice", PriceUtil.format((int) categoryTotalPrice));

        // 전체 주문 중 가장 많이 주문된 세탁 옵션
        List<OrderItemOption> allOrderItemsOptions = orderItemOptionRepository.findAllWithLaundryOption();
        Map<Long, Integer> optionCountMap = new HashMap<>();
        Map<Long, Integer> optionPriceMap = new HashMap<>();
        Map<Long, String> optionNameMap = new HashMap<>();

        for (OrderItemOption orderItemOption : allOrderItemsOptions) {
            Long optionId = orderItemOption.getLaundryOption().getId();
            int price = orderItemOption.getLaundryOption().getExtraPrice() != null ? orderItemOption.getLaundryOption().getExtraPrice() : 0;
            String optionName = orderItemOption.getLaundryOption().getName();

            optionCountMap.put(optionId, optionCountMap.getOrDefault(optionId, 0) + 1);
            optionPriceMap.put(optionId, price);
            optionNameMap.put(optionId, optionName);
        }

        int maxOptionCount = 0;
        int singleOptionPrice = 0;
        int optionTotalPrice = 0;
        String mostOrderedOptionName = "없음";

        for (Map.Entry<Long, Integer> entry : optionCountMap.entrySet()) {
            if (entry.getValue() > maxOptionCount) {
                maxOptionCount = entry.getValue();
                mostOrderedOptionName = optionNameMap.get(entry.getKey());
                optionTotalPrice = singleOptionPrice * entry.getValue();
            }
        }
        statistics.put("mostOrderedOptionName", mostOrderedOptionName);
        statistics.put("mostOrderedOptionCount", PriceUtil.format(maxOptionCount));
        statistics.put("mostOrderedOptionPrice", PriceUtil.format(singleOptionPrice));
        statistics.put("mostOrderedOptionTotalPrice", PriceUtil.format(optionTotalPrice));

        // 자재 재고 부족
        List<SupplyItem> lowStockItems = supplyItemRepository.findAll().stream()
                .filter(item -> item.getStockQuantity() != null
                        && item.getSafetyStock() != null
                        && item.getStockQuantity() <= item.getSafetyStock())
                .collect(Collectors.toList());

        List<Map<String, Object>> lowStockItemList = lowStockItems.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("name", item.getName());
                    itemMap.put("stockQuantity", item.getStockQuantity());
                    itemMap.put("safetyStock", item.getSafetyStock());
                    itemMap.put("unit", item.getUnit());
                    return itemMap;
                })
                .collect(Collectors.toList());

        statistics.put("lowStockItems", lowStockItemList);
        statistics.put("lowStockItemCount", PriceUtil.format(lowStockItems.size()));

        // 현재 진행형 주문(WASHING, DRYING)
        long washingCount = orderRepository.countByStatus(OrderStatus.WASHING);
        long dryingCount = orderRepository.countByStatus(OrderStatus.DRYING);
        long progressiveOrdersCount = washingCount + dryingCount;

        statistics.put("myProcessingOrders", progressiveOrdersCount);

        // 금일 완료된 주문
        LocalDate today = LocalDate.now();
        List<Order> completedOrders = orderRepository.findByStatus(OrderStatus.COMPLETED);

        if (completedOrders == null) {
            throw new Exception404("금일 완료된 주문 내역이 없습니다.");
        }

        long completedOrdersOfToday = 0;

        for (Order order : completedOrders) {
            if (order.getUpdatedAt() == null) {
                continue;
            }
            LocalDateTime localDateTime = order.getUpdatedAt().toLocalDateTime();
            LocalDate updatedDate = localDateTime.toLocalDate();
            if (updatedDate.isEqual(today)) {
                completedOrdersOfToday++;
            }
        }

        statistics.put("myCompletedOrders", completedOrdersOfToday);

        // 금일 접수된 주문
        List<Order> receivedOrders = orderRepository.findByStatus(OrderStatus.RECEIVED);
        if (receivedOrders == null) {
            throw new Exception404("접수된 주문 내역이 없습니다.");
        }

        long receivedOrdersOfToday = 0;

        for (Order order : receivedOrders) {
            if (order.getOrderDate().equals(LocalDate.now())) {
                receivedOrdersOfToday++;
            }
        }

        statistics.put("myTodayOrders", receivedOrdersOfToday);

        // 통계 전체 반환
        return statistics;
    }

}
