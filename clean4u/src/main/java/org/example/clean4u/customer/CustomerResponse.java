package org.example.clean4u.customer;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;

import java.util.Date;

public class CustomerResponse {

    @Data
    public static class SaveDTO {
        private Long id;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String memo;

        public SaveDTO() {}

        public SaveDTO(Customer customer) {
            this.id = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null ) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
        }
    } // end of static inner class

    @Data
    public static class ListDTO {
        private Long id;
        private String name;
        private Grade grade;
        private String phone;

        public ListDTO(Customer customer) {
            this.id = customer.getId();
            this.name = customer.getName();
            this.grade = customer.getGrade();
            this.phone = customer.getPhone();
        }
    }// end of static inner class

    @Data
    public static class DetailDTO {
        private Long id;
        private Grade grade;
        private String name;
        private String birth;
        private String phone;
        private String createdAt;
        private String memo;

        public DetailDTO(Customer customer) {
            this.id = customer.getId();
            this.grade = customer.getGrade();
            this.name = customer.getName();
            if (customer.getBirth() != null) {
                this.birth = DateUtil.birthFormat(customer.getBirth());
            }
            this.phone = customer.getPhone();
            this.createdAt = DateUtil.timestampFormat(customer.getCreatedAt());
            this.memo = customer.getMemo();
        }

        public String getMemoOrDash() {
            return memo == null ? "-" : memo;
        }
    }

}
