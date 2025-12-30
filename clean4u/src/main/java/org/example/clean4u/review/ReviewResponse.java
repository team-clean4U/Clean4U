package org.example.clean4u.review;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;

public class ReviewResponse {

    @Data
    public static class DetailDTO {
        private Long id;
        private Integer rating;
        private String comment;
        private Long orderId;
        private String customerName;
        private String createdAt;

        public DetailDTO(Review review) {
            this.id = review.getId();
            this.rating = review.getRating();
            this.comment = review.getComment();
            this.orderId = review.getOrder().getId();
            this.customerName = review.getOrder().getCustomer().getName();
            this.createdAt = DateUtil.timestampFormat(review.getCreatedAt());
        }
    }
}
