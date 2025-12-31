package org.example.clean4u.order;

import lombok.Data;
import org.example.clean4u._core.utils.PriceUtil;
import org.example.clean4u.customer.Grade;
import org.example.clean4u.orderItem.OrderItemResponse;
import org.example.clean4u.orderStatusHistory.OrderStatusHistoryResponse;
import org.example.clean4u.review.ReviewResponse;

import java.time.LocalDate;
import java.util.List;

public class OrderResponse {

    @Data
    public static class ListDTO {
        private Long orderId;
        private String customerName;
        private String phone;
        private Grade grade;
        private LocalDate orderDate;
        private OrderStatus status;

        public ListDTO(Order order) {
            this.orderId = order.getId();
            if(order.getCustomer() != null) {
                this.customerName = order.getCustomer().getName();
                this.phone = order.getCustomer().getPhone();
                this.grade = order.getCustomer().getGrade();
            }
            this.orderDate = order.getOrderDate();
            this.status = order.getStatus();
        }
    }

    @Data
    public static class DetailDTO {
        private Long orderId;
        private String customerName;
        private Grade grade;
        private LocalDate orderDate;
        private OrderStatus status;
        private boolean isCancelled;
        private String memo;
        private String laundryImage;

        private String totalPrice;
        private List<OrderItemResponse.DetailDto> items;
        private List<OrderStatusHistoryResponse.DetailDTO> histories;
        private String reviewLink;
        private boolean hasReview;
        private ReviewResponse.DetailDTO review;

        public DetailDTO(
                Order order,
                List<OrderItemResponse.DetailDto> items,
                List<OrderStatusHistoryResponse.DetailDTO> histories,
                ReviewResponse.DetailDTO review
        ) {
            this.orderId = order.getId();
            if(order.getCustomer() != null) {
                this.customerName = order.getCustomer().getName();
                this.grade = order.getCustomer().getGrade();
            }
            this.orderDate = order.getOrderDate();
            this.status = order.getStatus();
            this.isCancelled = order.getStatus() == OrderStatus.CANCELLED;
            this.memo = order.getMemo();
            this.laundryImage = order.getLaundryImage();
            this.totalPrice = PriceUtil.format(order.getTotalPrice());
            this.items = items;
            this.histories = histories;
            
            if (order.getReviewToken() != null) {
                this.reviewLink = "/review/save?token=" + order.getReviewToken();
                this.hasReview = review != null;
                this.review = review;
            }
        }
    }

    @Data
    public static class UpdateFormDTO {
        private Long orderId;
        private String customerName;
        private Grade grade;
        private OrderStatus status;
        private String memo;
        private List<OrderItemResponse.UpdateFormDto> items;

        public UpdateFormDTO(Order order, List<OrderItemResponse.UpdateFormDto> items) {
            this.orderId = order.getId();
            if(order.getCustomer() != null) {
                this.customerName = order.getCustomer().getName();
                this.grade = order.getCustomer().getGrade();
            }
            this.status = order.getStatus();
            this.memo = order.getMemo();
            this.items = items;
        }
    }
}
