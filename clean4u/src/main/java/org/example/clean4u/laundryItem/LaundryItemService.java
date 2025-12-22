package org.example.clean4u.laundryItem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaundryItemService {
    private final LaundryItemRepository repository;

    public List<LaundryItemResponse.ListDTO> getAllLaundryItems() {
        List<LaundryItem> laundryItemList = repository.findAllOrderByCreatedAtDesc();
        return laundryItemList.stream()
                .map(LaundryItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public LaundryItemResponse.DetailDTO getDetail(Long laundryItemId) {
        LaundryItem laundryItem = repository.findById(laundryItemId)
                .orElseThrow(() -> new Exception404("해당 세탁물을 찾을 수 없습니다."));
        return new LaundryItemResponse.DetailDTO(laundryItem);
    }

    @Transactional
    public LaundryItem save(@Valid LaundryItemRequest.SaveDTO saveDTO) {
        if (repository.findByName(saveDTO.getName()).isPresent()) {
            throw new Exception400("이미 존재하는 세탁물 이름입니다.");
        }
        LaundryItem laundryItem = saveDTO.toEntity();
        return repository.save(laundryItem);
    }

    public LaundryItemResponse.UpdateFormDTO getFormForUpdate(Long laundryItemId) {
        LaundryItem laundryItem = repository.findById(laundryItemId)
                .orElseThrow(() -> new Exception404("해당 세탁물을 찾을 수 없습니다."));
        return new LaundryItemResponse.UpdateFormDTO(laundryItem);
    }

    @Transactional
    public LaundryItem update(Long laundryItemId, @Valid LaundryItemRequest.UpdateDTO updateDTO) {
        LaundryItem laundryItem = repository.findById(laundryItemId)
                .orElseThrow(() -> new Exception404("해당 세탁물을 찾을 수 없습니다."));
        if (!laundryItem.getName().equals(updateDTO.getName())) {
            if (repository.findByName(updateDTO.getName()).isPresent()) {
                throw new Exception400("이미 존재하는 세탁물 이름입니다.");
            }
        }
        laundryItem.update(updateDTO);
        return laundryItem;
    }

    @Transactional
    public void delete(Long laundryItemId) {
        repository.findById(laundryItemId)
                .orElseThrow(() -> new Exception404("해당 세탁물을 찾을 수 없습니다."));
        repository.deleteById(laundryItemId);
    }

    public List<LaundryItemResponse.ListDTO> findByCategory(LaundryCategory category) {
        List<LaundryItem> laundryItemList = repository.findByCategory(category);
        return laundryItemList.stream()
                .map(LaundryItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public List<LaundryItemResponse.ListDTO> findByNameContaining(String name) {
        List<LaundryItem> laundryItemList = repository.findByNameContaining(name);
        return laundryItemList.stream()
                .map(LaundryItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public List<LaundryItemResponse.ListDTO> findByNameContainingAndCategory(LaundryCategory category, String name) {
        List<LaundryItem> laundryItemList = repository.findByNameContainingAndCategory(category, name);
        return laundryItemList.stream()
                .map(LaundryItemResponse.ListDTO::new)
                .collect(Collectors.toList());
    }
}
