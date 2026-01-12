package org.example.clean4u.order;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @PutMapping(value = "/{orderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderResponse.UpdateDTO> updateProc(@PathVariable Long orderId, @ModelAttribute @Valid OrderRequest.UpdateDTO updateDto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        boolean isGradeChanged = orderService.updateProc(orderId, updateDto, sessionUser.getId());
        return ResponseEntity.ok(
                new OrderResponse.UpdateDTO(
                        isGradeChanged,
                        isGradeChanged ? "고객 등급이 변경되었습니다." : "주문 수정 성공"
                )
        );
    }

    @DeleteMapping("/{orderId}/laundry-image")
    public ResponseEntity<?> deleteLaundryImage(@PathVariable Long orderId) {
        orderService.deleteLaundryImage(orderId);
        return ResponseEntity.ok().body(Map.of("message", "주문의 세탁물 사진이 삭제되었습니다"));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deactivate(@PathVariable Long orderId, @RequestParam(defaultValue = "false") boolean hardDelete, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        boolean existing = orderService.deactivate(orderId, sessionUser.getId(), hardDelete);
        if(!existing) {
            return ResponseEntity.ok().body(Map.of("message", "주문이 존재하지 않습니다."));
        }
        return ResponseEntity.ok().body(Map.of("message", hardDelete ? "주문이 완전히 삭제되었습니다." : "주문이 취소 처리(비활성화)되었습니다."));
    }

}
