package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@RestController
public class CustomerApiController {

    private final CustomerService customerService;

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse.UpdateDTO> updateCustomerInfo(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest.UpdateDTO updateDTO,
            HttpSession session
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        CustomerResponse.UpdateDTO result = customerService.update(customerId, updateDTO, sessionUser.getId());
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/{customerId}/deactivate")
    public ResponseEntity<CustomerResponse.DetailDTO> deactivateCustomer(@PathVariable Long customerId) {
        customerService.deactivateCustomer(customerId);
        CustomerResponse.DetailDTO result = customerService.getDetail(customerId);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/{customerId}/activate")
    public ResponseEntity<CustomerResponse.DetailDTO> activateCustomer(@PathVariable Long customerId) {
        customerService.activateCustomer(customerId);
        CustomerResponse.DetailDTO result = customerService.getDetail(customerId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.delete(customerId);

        return ResponseEntity.noContent().build();
    }
}
