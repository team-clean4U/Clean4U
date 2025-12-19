package org.example.clean4u.supplyItem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception400;
import org.example.clean4u._core.exception.Exception404;
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
                .orElseThrow(() -> new Exception404("해당 자재를 찾을 수 없습니다."));
        return new SupplyItemResponse.DetailDTO(supplyItem);
    }

    @Transactional
    public SupplyItem save(@Valid SupplyItemRequest.SaveDTO saveDTO) {
        if (repository.findByName(saveDTO.getName()).isPresent()) {
            throw new Exception400("이미 존재하는 자재 이름입니다.");
        }

        SupplyItem supplyItem = saveDTO.toEntity();
        return repository.save(supplyItem);
    }

    public SupplyItemResponse.UpdateFormDTO getFormForUpdate(Long supplyItemId) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 자재를 찾을 수 없습니다."));
        return new SupplyItemResponse.UpdateFormDTO(supplyItem);
    }

    @Transactional
    public SupplyItem update(Long supplyItemId, @Valid SupplyItemRequest.UpdateDTO updateDTO) {
        SupplyItem supplyItem = repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 자재를 찾을 수 없습니다."));
        if (!supplyItem.getName().equals(updateDTO.getName())) {
            if (repository.findByName(updateDTO.getName()).isPresent()) {
                throw new Exception400("이미 존재하는 자재 이름입니다.");
            }
        }
        supplyItem.update(updateDTO);
        return supplyItem;
    }

    @Transactional
    public void delete(Long supplyItemId) {
        repository.findById(supplyItemId)
                .orElseThrow(() -> new Exception404("해당 자재를 찾을 수 없습니다."));
        repository.deleteById(supplyItemId);
    }

    public List<SupplyItemResponse.ListDTO> findByNameContaining(String name) {
        List<SupplyItem> supplyItemList = repository.findByNameContaining(name);
        return supplyItemList.stream()
                .map(SupplyItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public List<SupplyItemResponse.ListDTO> findLowStockItems() {
        List<SupplyItem> supplyItemList = repository.findLowStockItems();
        return supplyItemList.stream()
                .map(SupplyItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public List<SupplyItemResponse.ListDTO> findSafetyStockItems() {
        List<SupplyItem> supplyItemList = repository.findSafetyStockItems();
        return supplyItemList.stream()
                .map(SupplyItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }
}
