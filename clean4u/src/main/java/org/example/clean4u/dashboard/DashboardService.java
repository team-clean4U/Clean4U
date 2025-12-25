package org.example.clean4u.dashboard;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.utils.PriceUtil;
import org.example.clean4u.laundryItem.LaundryCategory;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.orderItem.OrderItem;
import org.example.clean4u.orderItem.OrderItemRepository;
import org.example.clean4u.orderItemOption.OrderItemOptionRepository;
import org.example.clean4u.supplyItem.SupplyItemRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 통게 전체 반환
        return statistics;
    }
}
