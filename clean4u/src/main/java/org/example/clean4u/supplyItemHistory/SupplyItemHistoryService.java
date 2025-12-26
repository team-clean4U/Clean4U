package org.example.clean4u.supplyItemHistory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.supplyItem.SupplyItem;
import org.example.clean4u.supplyItem.SupplyItemRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplyItemHistoryService {
    private final SupplyItemHistoryRepository historyRepository;
    private final SupplyItemRepository supplyItemRepository;

    public SupplyItemHistoryResponse.GroupDetailDTO getDetail(Long historyId) {
        SupplyItemHistory firstItemHistory = historyRepository.findById(historyId)
                .orElseThrow(() -> new Exception404("해당 재고 이력을 찾을 수 없습니다."));

        List<SupplyItemHistory> groupedHistories = historyRepository.findGroupedHistories(historyId);
        return new SupplyItemHistoryResponse.GroupDetailDTO(firstItemHistory, groupedHistories);
    }


    @Transactional
    public void save(@Valid SupplyItemHistoryRequest.SaveDTO saveDTO, Employee employee) {
        for (SupplyItemHistoryRequest.ItemDTO item : saveDTO.getItems()) {
            SupplyItem supplyItem = supplyItemRepository.findById(item.getSupplyItemId())
                    .orElseThrow(() -> new Exception404("해당 자재를 찾을 수 없습니다."));

            Integer stockBefore = supplyItem.getStockQuantity();
            Integer stockAfter;

            switch (saveDTO.getType()) {
                case IN:
                    stockAfter = stockBefore + item.getQuantity();
                    break;
                case OUT:
                    if (stockBefore < item.getQuantity()) {
                        throw new Exception400("출고 수량이 현재 재고보다 많습니다.");
                    }
                    stockAfter = stockBefore - item.getQuantity();
                    break;
                case ADJUSTMENT:
                    stockAfter = item.getQuantity();
                    break;
                default:
                    throw new Exception400("잘못된 거래 유형입니다.");
            }

            SupplyItemHistory history = SupplyItemHistory.builder()
                    .supplyItem(supplyItem)
                    .type(saveDTO.getType())
                    .quantity(item.getQuantity())
                    .stockBefore(stockBefore)
                    .stockAfter(stockAfter)
                    .memo(saveDTO.getMemo())
                    .employee(employee)
                    .build();

            historyRepository.save(history);
            supplyItem.updateStockQuantity(stockAfter);
        }
    }


    public PageResponse<SupplyItemHistoryResponse.ListDTO> supplyItemHistoryList(
            int page,
            int size,
            Type type,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));
        Pageable pageable =PageRequest.of(validPage, validSize);

        String typeStr = type != null ? type.name() : null;
        Page<Object[]> groupedPage = historyRepository.findAllWithFilters(typeStr, fromDate, toDate, pageable);

        List<Long> historyIds = groupedPage.getContent().stream()
                .map(r -> ((Number) r[0]).longValue())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, SupplyItemHistory> historyMap = historyRepository.findAllById(historyIds).stream()
                .collect(Collectors.toMap(SupplyItemHistory::getId, h -> h));

        List<SupplyItemHistoryResponse.ListDTO> content = groupedPage.getContent().stream()
                .map(r -> {
                    Long firstHistoryId = ((Number) r[0]).longValue();
                    Integer itemCount = ((Number) r[1]).intValue();
                    SupplyItemHistory firstHistory = historyMap.get(firstHistoryId);
                    return firstHistory != null ? new SupplyItemHistoryResponse.ListDTO(firstHistory, itemCount) : null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        Page<SupplyItemHistoryResponse.ListDTO> listDTOPage = new PageImpl<>(content, pageable, groupedPage.getTotalElements());

        return new PageResponse<>(listDTOPage, dto -> dto);
    }
}
