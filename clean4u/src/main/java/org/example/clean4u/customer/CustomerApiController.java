package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@RestController
public class CustomerApiController {

    private final CustomerService customerService;

    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse.UpdateDTO>> updateCustomerInfo(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest.UpdateDTO updateDTO,
            HttpSession session
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        CustomerResponse.UpdateDTO result = customerService.update(customerId, updateDTO, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerId) {
        customerService.delete(customerId);

        return ResponseEntity.ok(ApiResponse.ok("삭제가 완료되었습니다"));
    }
}
