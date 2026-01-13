package org.example.clean4u.client;

import lombok.Data;
import org.example.clean4u._core.utils.PriceUtil;

public class ClientResponse {

    @Data
    public static class OrderStats {
        private final int totalOrders;
        private final int totalAmount;
        private final int completedOrders;
        private final int pendingOrders;
        private final String formattedTotalAmount;

        public OrderStats(int totalOrders, int totalAmount, int completedOrders, int pendingOrders) {
            this.totalOrders = totalOrders;
            this.totalAmount = totalAmount;
            this.completedOrders = completedOrders;
            this.pendingOrders = pendingOrders;
            this.formattedTotalAmount = PriceUtil.format(totalAmount);
        }
    }
}

