package org.example.clean4u.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.clean4u.order.Order;

public class ReviewRequest {

    @Data
    public static class SaveDTO {
        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        private Integer rating;

        @Size(max = 255, message = "댓글은 255자 이하여야 합니다.")
        private String comment;

        public Review toEntity(Order order) {
            return Review.builder()
                    .order(order)
                    .rating(rating)
                    .comment(comment)
                    .build();
        }
    }
}
