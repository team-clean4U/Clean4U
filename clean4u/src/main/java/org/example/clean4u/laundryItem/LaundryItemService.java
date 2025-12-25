package org.example.clean4u.laundryItem;

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

    public PageResponse<LaundryItemResponse.ListDTO> laundryItemList(
            int page,
            int size,
            LaundryCategory category,
            String name
    ) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<LaundryItem> laundryItemPage;
        boolean hasName = name != null && !name.isBlank();

        if (category != null && hasName) {
            laundryItemPage = repository.findByNameContainingAndCategory(category, name, pageable);
        } else if (category != null) {
            laundryItemPage = repository.findByCategory(category, pageable);
        } else if (hasName) {
            laundryItemPage = repository.findByNameContaining(name, pageable);
        } else {
            laundryItemPage = repository.findAllOrderByCreatedAtDesc(pageable);
        }

        return new PageResponse<>(laundryItemPage, LaundryItemResponse.ListDTO::new);
    }
}
