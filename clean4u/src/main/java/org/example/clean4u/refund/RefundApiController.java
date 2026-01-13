package org.example.clean4u.refund;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.ApiResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refunds")
public class RefundApiController {
    private final RefundService refundService;

    @PostMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> refundRequestProc(@PathVariable Long paymentId,
                                                               @RequestBody @Valid RefundRequest.DetailDTO detailDTO,
                                                               HttpSession session) {
        Employee employee = (Employee) session.getAttribute("sessionUser");
        if(employee == null) {
            throw new Exception404("로그인한 사용자를 찾을 수 없습니다.");
        }
        refundService.refundRequestProc(paymentId, detailDTO, employee);
        return ResponseEntity.ok().body(ApiResponse.ok("환불이 완료되었습니다."));
    }
}
