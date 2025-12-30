package org.example.clean4u.dashboard;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.utils.PriceUtil;
import org.example.clean4u.customer.CustomerRepository;
import org.example.clean4u.customer.Grade;
import org.example.clean4u.laundryItem.LaundryCategory;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderStatus;
import org.example.clean4u.orderItem.OrderItem;
import org.example.clean4u.orderItem.OrderItemRepository;
import org.example.clean4u.orderItemOption.OrderItemOption;
import org.example.clean4u.orderItemOption.OrderItemOptionRepository;
import org.example.clean4u.orderStatusHistory.OrderStatusHistoryRepository;
import org.example.clean4u.supplyItem.SupplyItem;
import org.example.clean4u.supplyItem.SupplyItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final SupplyItemRepository supplyItemRepository;
    private final CustomerRepository customerRepository;

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
        Long mostOrderedOptionId = null;

        for (Map.Entry<Long, Integer> entry : optionCountMap.entrySet()) {
            if (entry.getValue() > maxOptionCount) {
                maxOptionCount = entry.getValue();
                mostOrderedOptionId = entry.getKey();
                mostOrderedOptionName = optionNameMap.get(entry.getKey());
            }
        }

        if (mostOrderedOptionId != null) {
            singleOptionPrice = optionPriceMap.getOrDefault(mostOrderedOptionId, 0);
            optionTotalPrice = singleOptionPrice * maxOptionCount;
        }
        statistics.put("mostOrderedOptionName", mostOrderedOptionName);
        statistics.put("mostOrderedOptionCount", PriceUtil.format(maxOptionCount));
        statistics.put("mostOrderedOptionPrice", PriceUtil.format(singleOptionPrice));
        statistics.put("mostOrderedOptionTotalPrice", PriceUtil.format(optionTotalPrice));

        // 비품 재고 부족
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
        int washingCount = orderRepository.countByStatus(OrderStatus.WASHING.name());
        int dryingCount = orderRepository.countByStatus(OrderStatus.DRYING.name());
        int progressiveOrdersCount = washingCount + dryingCount;

        statistics.put("washing", OrderStatus.WASHING);
        statistics.put("washingOrders", PriceUtil.format(washingCount));
        statistics.put("drying", OrderStatus.DRYING);
        statistics.put("dryingOrders", PriceUtil.format(dryingCount));

        statistics.put("processingOrders", PriceUtil.format(progressiveOrdersCount));

        // 평균 처리 경과 시간
        Double avgMinutes = historyRepository.findAverageProcessingMinutes();

        if (avgMinutes == null) {
            statistics.put("averageTime", "0 분");
        } else {
            long days = (long) (avgMinutes / (24 * 60));
            long hours = (long) ((avgMinutes % (24 * 60)) / 60);
            long minutes = (long) (avgMinutes % 60);

            StringBuilder sb = new StringBuilder();
            if (days > 0) {
                sb.append(days).append("일 ");
            }
            if (hours > 0) {
                sb.append(hours).append("시간 ");
            }
            if (minutes > 0) {
                sb.append(minutes).append("분");
            }
            statistics.put("averageTime", sb.toString().trim());
        }

        // 금일 완료된 주문
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        Integer completedOrders = orderRepository.countByStatusAndDate(OrderStatus.COMPLETED.name(), start, end);
        statistics.put("completedOrders", PriceUtil.format(completedOrders == null ? 0 : completedOrders));

        // 금일 접수된 주문, 금일 매출
        Integer todayOrders = orderRepository.countTodayOrders();
        Integer todaySales = orderRepository.countPriceTodayOrders();
        statistics.put("todayOrders", PriceUtil.format(todayOrders == null ? 0 : todayOrders));
        statistics.put("todaySales", PriceUtil.format(todaySales == null ? 0 : todaySales));

        // 고객 등급별 평균 소비 금액
        List<Map<String, Object>> avgList = new ArrayList<>();

        for (Grade grade : Grade.values()) {
            Integer avg = customerRepository.findAverageTotalPriceByGrade(grade.name());
            if (avg != null) {
                Map<String, Object> row = new HashMap<>();
                row.put("grade", grade.name());
                row.put("avg", PriceUtil.format(avg));
                avgList.add(row);
            }
        }

        statistics.put("avgTotalPriceByGrade", avgList);

        // 통계 전체 반환
        return statistics;
    }

}
