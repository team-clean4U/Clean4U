package org.example.clean4u.order;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @PutMapping(value = "/{orderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OrderResponse.UpdateDTO>> updateProc(@PathVariable Long orderId,
                                                                           @ModelAttribute @Valid OrderRequest.UpdateDTO updateDto,
                                                                           HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        boolean isGradeChanged = orderService.updateProc(orderId, updateDto, sessionUser.getId());
        OrderResponse.UpdateDTO result
                = new OrderResponse.UpdateDTO(isGradeChanged, isGradeChanged
                ? "고객 등급이 변경되었습니다."
                : "주문 수정이 완료되었습니다.");
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @DeleteMapping("/{orderId}/laundry-image")
    public ResponseEntity<ApiResponse<Void>> deleteLaundryImage(@PathVariable Long orderId) {
        orderService.deleteLaundryImage(orderId);
        return ResponseEntity.ok(ApiResponse.ok("주문 이미지가 삭제되었습니다."));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long orderId,
                                                        @RequestParam(defaultValue = "false") boolean hardDelete,
                                                        HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        orderService.deactivate(orderId, sessionUser.getId(), hardDelete);
        return ResponseEntity.ok().body(ApiResponse.ok(hardDelete ? "주문이 완전히 삭제되었습니다." : "주문이 취소 처리(비활성화)되었습니다."));
    }

}
