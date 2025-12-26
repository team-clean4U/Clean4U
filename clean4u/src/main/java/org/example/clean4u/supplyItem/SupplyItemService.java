package org.example.clean4u.supplyItem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplyItemService {
    private final SupplyItemRepository repository;

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
    public SupplyItem save(@Valid SupplyItemRequest.SaveDTO saveDTO) {
        if (repository.findByName(saveDTO.getName()).isPresent()) {
            throw new Exception400("이미 존재하는 비품 이름입니다.");
        }

        SupplyItem supplyItem = saveDTO.toEntity();
        return repository.save(supplyItem);
    }

    public SupplyItemResponse.UpdateFormDTO getFormForUpdate(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        return new SupplyItemResponse.UpdateFormDTO(supplyItem);
    }

    @Transactional
    public SupplyItem update(Long supplyItemId, @Valid SupplyItemRequest.UpdateDTO updateDTO) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        if (!supplyItem.getName().equals(updateDTO.getName())) {
            if (repository.findByName(updateDTO.getName()).isPresent()) {
                throw new Exception400("이미 존재하는 비품 이름입니다.");
            }
        }
        supplyItem.update(updateDTO);
        return supplyItem;
    }

    @Transactional
    public void deactivate(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 비품을 찾을 수 없습니다."));
        supplyItem.updateIsActive(false);
    }

    @Transactional
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
}
