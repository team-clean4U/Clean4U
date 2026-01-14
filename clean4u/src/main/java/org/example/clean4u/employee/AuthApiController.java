package org.example.clean4u.employee;

import com.solapi.shadow.retrofit2.http.PATCH;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class AuthApiController {

    private final AuthService authService;

    @GetMapping("/admin/option-chart")
    public List<Object[]> optionChart() {
        return authService.optionChart();
    }

    @PATCH("/employees/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long employeeId,
            @RequestBody EmployeeRequest.UpdateStatus status
    ) {
        UserStatus userStatus = UserStatus.valueOf(status.getStatus().toUpperCase());
        authService.updateStatus(employeeId, userStatus);
        return ResponseEntity.ok(ApiResponse.ok("직원 상태 변경 완료"));
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long employeeId) {

        authService.delete(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("직원 삭제 완료"));
    }
}
