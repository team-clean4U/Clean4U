package org.example.clean4u.customer;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderStatus;

import java.time.LocalDate;
import java.util.Date;
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

        public ListDTO(Customer customer) {
            this.customerId = customer.getId();
            this.name = customer.getName();
            this.grade = customer.getGrade();
            this.phone = customer.getPhone();
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
        private List<OrderDTO> orders;

        public DetailDTO(Customer customer, List<Order> orderList) {
            this.customerId = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
            this.phone = customer.getPhone();
            this.createdAt = DateUtil.timestampFormat(customer.getCreatedAt());
            this.memo = customer.getMemo();
            if (orders != null) {
                this.orders = orderList.stream()
                        .map(OrderDTO::new)
                        .toList();
            }
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
        private Integer totalPrice;

        public OrderDTO(Order order) {
            this.orderId = order.getId();
            this.orderDate = order.getOrderDate();
            this.status = order.getStatus();
            this.totalPrice = order.getTotalPrice();
        }
    }

    @Data
    public static class UpdateDTO {
        private Long customerId;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String createdAt;
        private String memo;

        public UpdateDTO(Customer customer) {
            this.customerId = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
            this.phone = customer.getPhone();
            this.createdAt = DateUtil.timestampFormat(customer.getCreatedAt());
            this.memo = customer.getMemo();
        }
    }

    @Data
    public static class UpdateViewDTO {
        private Long customerId;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String memo;

        public UpdateViewDTO(Customer customer) {
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
