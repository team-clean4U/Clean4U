package org.example.clean4u.supplyItemHistory;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/supply-item-histories")
@RequiredArgsConstructor
public class SupplyItemHistoryApiController {

    private final SupplyItemHistoryService service;

    // GET /api/v1/supply-item-histories
    @GetMapping
    public ResponseEntity<PageResponse<SupplyItemHistoryResponse.ListDTO>> getSupplyItemHistoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int pageIndex = Math.max(0, page - 1);
        PageResponse<SupplyItemHistoryResponse.ListDTO> result = service.supplyItemHistoryList(pageIndex, size, type, fromDate, toDate);
        return ResponseEntity.ok().body(result);
    }

    // GET /api/supply-item-histories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SupplyItemHistoryResponse.GroupDetailDTO> getSupplyItemHistoryById(@PathVariable Long id) {
        SupplyItemHistoryResponse.GroupDetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }
}
