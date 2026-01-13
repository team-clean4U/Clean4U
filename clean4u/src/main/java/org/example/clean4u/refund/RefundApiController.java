package org.example.clean4u.refund;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refunds")
public class RefundApiController {
    private final RefundService refundService;

    @PostMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> refundRequestProc(@PathVariable Long paymentId, RefundRequest.DetailDTO detailDTO) {
        refundService.refundRequestProc(paymentId, detailDTO);
        return ResponseEntity.ok().body(ApiResponse.ok("환불이 완료되었습니다."));
    }
}
