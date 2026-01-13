package org.example.clean4u.supplyItem;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/supply-items")
@RequiredArgsConstructor
public class SupplyItemApiController {

    private final SupplyItemService service;

    // PUT /api/v1/supply-items/{supplyItemId}
    @PutMapping("/{supplyItemId}")
    public ResponseEntity<ApiResponse<SupplyItemResponse.DetailDTO>> updateSupplyItem(
            @PathVariable Long supplyItemId,
            @Valid @RequestBody SupplyItemRequest.UpdateDTO updateDTO,
            HttpSession session
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        service.update(supplyItemId, updateDTO, sessionUser);
        SupplyItemResponse.DetailDTO result = service.getDetail(supplyItemId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
