package org.example.clean4u.supplyItem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.supplyItemHistory.SupplyItemHistory;
import org.example.clean4u.supplyItemHistory.SupplyItemHistoryRepository;
import org.example.clean4u.supplyItemHistory.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplyItemService {
    private final SupplyItemRepository repository;
    private final SupplyItemHistoryRepository historyRepository;

    public List<SupplyItemResponse.ListDTO> getAllSupplyItems() {
        List<SupplyItem> supplyItemList = repository.findAllOrderByCreatedAtDesc();
        return supplyItemList.stream()
                .map(SupplyItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public SupplyItemResponse.DetailDTO getDetail(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        return new SupplyItemResponse.DetailDTO(supplyItem);
    }

    @Transactional
    @CacheEvict(value = "lowStockItems", allEntries = true)
    public void save(@Valid SupplyItemRequest.SaveDTO saveDTO, Employee employee) {

        Type type = saveDTO.getHistoryType();

        for (SupplyItemRequest.ItemDTO item : saveDTO.getItems()) {
            SupplyItem supplyItem;
            Integer stockBefore;
            Integer stockAfter;
            Integer quantity;

            switch (type) {
                case IN:
                    supplyItem = repository.findByName(item.getName()).orElse(null);

                    if (supplyItem == null) {
                        supplyItem = repository.save(
                                SupplyItem.builder()
                                        .name(item.getName())
                                        .unit(item.getUnit())
                                        .stockQuantity(0)
                                        .safetyStock(item.getSafetyStock())
                                        .isActive(item.getIsActive())
                                        .build());
                    }

                    stockBefore = supplyItem.getStockQuantity();
                    stockAfter = stockBefore + item.getStockQuantity();
                    quantity = item.getStockQuantity();
                    break;

                case OUT:
                    supplyItem = repository.findByName(item.getName())
                            .orElseThrow(() -> new Exception404("출고할 비품이 존재하지 않습니다." + item.getName()));

                    stockBefore = supplyItem.getStockQuantity();
                    if (stockBefore < item.getStockQuantity()) {
                        throw new Exception400("출고 수량이 현재 재고 수량보다 많습니다.");
                    }

                    stockAfter = stockBefore - item.getStockQuantity();
                    quantity = item.getStockQuantity();
                    break;

                case ADJUSTMENT:
                    supplyItem = repository.findByName(item.getName())
                            .orElseThrow(() -> new Exception404("조정할 비품이 존재하지 않습니다." + item.getName()));
                    stockBefore = supplyItem.getStockQuantity();
                    stockAfter = item.getStockQuantity();
                    quantity = stockAfter - stockBefore;
                    break;

                default:
                    throw new Exception400("잘못된 거래 유형입니다.");
            }

            supplyItem.updateStockQuantity(stockAfter);

            SupplyItemHistory history = SupplyItemHistory.builder()
                    .supplyItem(supplyItem)
                    .type(type)
                    .quantity(quantity)
                    .stockBefore(stockBefore)
                    .stockAfter(stockAfter)
                    .memo(saveDTO.getMemo())
                    .employee(employee)
                    .build();

            historyRepository.save(history);
        }
    }

    public SupplyItemResponse.UpdateFormDTO getFormForUpdate(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        return new SupplyItemResponse.UpdateFormDTO(supplyItem);
    }

    @Transactional
    @CacheEvict(value = "lowStockItems", allEntries = true)
    public SupplyItem update(Long supplyItemId, @Valid SupplyItemRequest.UpdateDTO updateDTO, Employee employee) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));

        Integer stockBefore = supplyItem.getStockQuantity();
        Integer stockAfter = updateDTO.getStockQuantity();

        if (!stockBefore.equals(stockAfter)) {
            Type historyType = updateDTO.getHistoryType();
            if (historyType == null) {
                throw new Exception400("재고가 변경될 경우 거래 유형을 선택해주세요.");
            }
            if (historyType == Type.UPDATE) {
                throw new Exception400("재고가 변경될 경우 입고, 출고, 조정 중 하나를 선택해주세요.");
            }

            if (stockAfter < stockBefore && historyType == Type.IN) {
                throw new Exception400("재고가 감소할 경우 입고를 선택할 수 없습니다. 출고 또는 조정을 선택해주세요.");
            }

            if (stockAfter > stockBefore && historyType == Type.OUT) {
                throw new Exception400("재고가 증가할 경우 출고를 선택할 수 없습니다. 입고 또는 조정을 선택해주세요.");
            }
        }

        if (!supplyItem.getName().equals(updateDTO.getName())) {
            if (repository.findByName(updateDTO.getName()).isPresent()) {
                throw new Exception400("이미 존재하는 비품 이름입니다.");
            }
        }

        supplyItem.update(updateDTO);

        Type historyType;
        Integer quantity;
        String memo = updateDTO.getMemo();

        if (!stockBefore.equals(stockAfter)) {
            historyType = updateDTO.getHistoryType();
            quantity = stockAfter - stockBefore;
        } else {
            historyType = Type.UPDATE;
            quantity = 0;
        }

        SupplyItemHistory history = SupplyItemHistory.builder()
                .supplyItem(supplyItem)
                .type(historyType)
                .quantity(quantity)
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .memo(memo)
                .employee(employee)
                .build();

        historyRepository.save(history);

        return supplyItem;
    }

    @Transactional
    @CacheEvict(value = "lowStockItems", allEntries = true)
    public void deactivate(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        supplyItem.updateIsActive(false);
    }

    @Transactional
    @CacheEvict(value = "lowStockItems", allEntries = true)
    public void activate(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        supplyItem.updateIsActive(true);
    }

    public PageResponse<SupplyItemResponse.ListDTO> supplyItemList(
            int page,
            int size,
            Boolean lowStock,
            String name
    ) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<SupplyItem> supplyItemPage;
        boolean hasName = name != null && !name.isBlank();

        if (hasName && lowStock != null && lowStock) {
            supplyItemPage = repository.findByNameContainingAndLowStock(name, pageable);
        } else if (hasName && lowStock != null && !lowStock) {
            supplyItemPage = repository.findByNameContainingAndSafetyStock(name, pageable);
        } else if (hasName) {
            supplyItemPage = repository.findByNameContaining(name, pageable);
        } else if (lowStock != null && lowStock) {
            supplyItemPage = repository.findLowStockItems(pageable);
        } else if (lowStock != null && !lowStock) {
            supplyItemPage = repository.findSafetyStockItems(pageable);
        } else {
            supplyItemPage = repository.findAllOrderByCreatedAtDesc(pageable);
        }
        return new PageResponse<>(supplyItemPage, SupplyItemResponse.ListDTO::new);
    }

    @Cacheable(value = "lowStockItems")
    public List<SupplyItemResponse.ListDTO> getLowStockItems() {
        return repository.findAllLowStockItems().stream()
                .map(SupplyItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }
}
