package org.example.clean4u.supplyItem;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/supply-items")
@RequiredArgsConstructor
public class SupplyItemApiController {

    private final SupplyItemService service;

    // GET /api/v1/supply-items
    @GetMapping
    public ResponseEntity<PageResponse<SupplyItemResponse.ListDTO>> getSupplyItemList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(required = false) String name
    ) {
        int pageIndex = Math.max(0, page - 1);
        PageResponse<SupplyItemResponse.ListDTO> result = service.supplyItemList(pageIndex, size, lowStock, name);
        return ResponseEntity.ok().body(result);
    }

    // GET /api/v1/supply-items/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SupplyItemResponse.DetailDTO> getSupplyItemById(@PathVariable Long id) {
        SupplyItemResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // POST /api/v1/supply-items
    @PostMapping
    public ResponseEntity<?> createSupplyItem(
            @Valid @RequestBody SupplyItemRequest.SaveDTO saveDTO,
            HttpSession session
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        service.save(saveDTO, sessionUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT /api/v1/supply-items/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SupplyItemResponse.DetailDTO> updateSupplyItem(
            @PathVariable Long id,
            @Valid @RequestBody SupplyItemRequest.UpdateDTO updateDTO,
            HttpSession session
    ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        service.update(id, updateDTO, sessionUser);
        SupplyItemResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // PATCH /api/v1/supply-items/{id}/deactivate
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SupplyItemResponse.DetailDTO> deactivateSupplyItem(@PathVariable Long id) {
        service.deactivate(id);
        SupplyItemResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // PATCH /api/v1/supply-items/{id}/activate
    @PatchMapping("/{id}/activate")
    public ResponseEntity<SupplyItemResponse.DetailDTO> activateSupplyItem(@PathVariable Long id) {
        service.activate(id);
        SupplyItemResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }
}
