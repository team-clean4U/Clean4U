package org.example.clean4u.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.example.clean4u.order.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewResponse.DetailDTO getDetailByOrderId(Long orderId) {
        Review review = reviewRepository.findByOrderId(orderId)
                .orElseThrow(() -> new Exception404("리뷰를 찾을 수 없습니다."));

        return new ReviewResponse.DetailDTO(review);
    }

    @Transactional
    public String generateReviewToken(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception404("주문을 찾을 수 없습니다."));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new Exception400("세탁이 완료된 주문만 리뷰 토큰을 생성할 수 있습니다.");
        }

        if (order.getReviewToken() != null) {
            return order.getReviewToken();
        }

        String token = UUID.randomUUID().toString();
        order.setReviewToken(token);
        orderRepository.save(order);

        return token;
    }

    @Transactional
    public Review save(@Valid ReviewRequest.SaveDTO saveDTO, String token) {
        Order order = orderRepository.findByReviewToken(token)
                .orElseThrow(() -> new Exception404("유효하지 않은 토큰입니다."));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new Exception400("세탁이 완료된 주문만 리뷰를 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new Exception400("이미 리뷰가 작성된 주문입니다.");
        }

        Review review = saveDTO.toEntity(order);
        return reviewRepository.save(review);
    }
}
