package org.example.clean4u.orderStatusHistory;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.order.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderStatusHistoryService {
    private final OrderStatusHistoryRepository historyRepository;

    public PageResponse<OrderStatusHistoryResponse.DetailDTO> orderStatusHistoryList(
            int page,
            int size,
            OrderRequest.SearchDTO searchDTO) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<OrderStatusHistory> statusPage = historyRepository.searchHistories(pageable, searchDTO);
        return new PageResponse<>(statusPage, OrderStatusHistoryResponse.DetailDTO::from);
    }
}
