package org.example.clean4u.customer;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;
import org.example.clean4u._core.utils.PriceUtil;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerResponse {

    @Data
    public static class SaveDTO {
        private Long customerId;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String memo;

        public SaveDTO() {}

        public SaveDTO(Customer customer) {
            this.customerId = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null ) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
        }
    } // end of static inner class

    @Data
    public static class ListDTO {
        private Long customerId;
        private String name;
        private Grade grade;
        private String phone;
        private Boolean isActive;

        public ListDTO(Customer customer) {
            this.customerId = customer.getId();
            this.name = customer.getName();
            this.grade = customer.getGrade();
            this.phone = customer.getPhone();
            this.isActive = customer.getIsActive();
        }
    }// end of static inner class

    @Data
    public static class DetailDTO {
        private Long customerId;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String createdAt;
        private String memo;
        private Boolean isActive;
        private List<OrderDTO> orders;

        public DetailDTO(Customer customer, List<OrderDTO> orderList) {
            this.customerId = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
            this.phone = customer.getPhone();
            this.createdAt = DateUtil.timestampFormat(customer.getCreatedAt());
            this.memo = customer.getMemo();
            this.isActive = customer.getIsActive();
            this.orders = orderList;
        }
        public String getMemoOrDash() {
            return memo == null ? "-" : memo;
        }
    }

    @Data
    public static class OrderDTO {
        private Long orderId;
        private LocalDate orderDate;
        private OrderStatus status;
        private String totalPrice;

        public OrderDTO(Order order) {
            this.orderId = order.getId();
            this.orderDate = order.getOrderDate();
            this.status = order.getStatus();
            if (order.getTotalPrice() != null) {
                this.totalPrice = PriceUtil.format(order.getTotalPrice());
            }
        }
    }

    @Data
    public static class UpdateDTO {
        private Long customerId;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String memo;

        public UpdateDTO() {}

        public UpdateDTO(Customer customer) {
            this.customerId = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
            this.phone = customer.getPhone();
            this.memo = customer.getMemo();
        }
    }
}
