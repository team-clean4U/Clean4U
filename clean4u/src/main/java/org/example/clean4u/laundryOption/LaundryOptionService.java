package org.example.clean4u.laundryOption;

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
public class LaundryOptionService {
    private final LaundryOptionRepository repository;

    public List<LaundryOptionResponse.ListDTO> getAllLaundryOptions() {
        List<LaundryOption> laundryOptionList = repository.findAllOrderByCreatedAtDesc();
        return laundryOptionList.stream()
                .map(LaundryOptionResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public LaundryOptionResponse.DetailDTO getDetail(Long laundryOptionId) {
        LaundryOption laundryOption = repository.findById(laundryOptionId)
                .orElseThrow(() -> new Exception404("해당 세탁물 옵션을 찾을 수 없습니다."));
        return new LaundryOptionResponse.DetailDTO(laundryOption);
    }

    @Transactional
    public LaundryOption save(@Valid LaundryOptionRequest.SaveDTO saveDTO) {
        if (repository.findByName(saveDTO.getName()).isPresent()) {
            throw new Exception400("이미 존재하는 세탁물 옵션 이름입니다.");
        }
        LaundryOption laundryOption = saveDTO.toEntity();
        return repository.save(laundryOption);
    }

    public LaundryOptionResponse.UpdateFormDTO getFormForUpdate(Long laundryOptionId) {
        LaundryOption laundryOption = repository.findById(laundryOptionId)
                .orElseThrow(() -> new Exception404("해당 세탁물 옵션을 찾을 수 없습니다."));
        return new LaundryOptionResponse.UpdateFormDTO(laundryOption);
    }

    @Transactional
    public LaundryOption update(Long laundryOptionId, @Valid LaundryOptionRequest.UpdateDTO updateDTO) {
        LaundryOption laundryOption = repository.findById(laundryOptionId)
                .orElseThrow(() -> new Exception404("해당 세탁물 옵션을 찾을 수 없습니다."));
        if (!laundryOption.getName().equals(updateDTO.getName())) {
            if (repository.findByName(updateDTO.getName()).isPresent()) {
                throw new Exception400("이미 존재하는 세탁물 옵션 이름입니다.");
            }
        }
        laundryOption.update(updateDTO);
        return laundryOption;
    }

    @Transactional
    public void deactivate(Long laundryOptionId) {
        LaundryOption laundryOption = repository.findById(laundryOptionId)
                .orElseThrow(() -> new Exception404("해당 세탁물 옵션을 찾을 수 없습니다."));
        laundryOption.updateIsActive(false);
    }

    @Transactional
    public void activate(Long laundryOptionId) {
        LaundryOption laundryOption = repository.findById(laundryOptionId)
                .orElseThrow(() -> new Exception404("해당 세탁물 옵션을 찾을 수 없습니다."));
        laundryOption.updateIsActive(true);
    }

    public PageResponse<LaundryOptionResponse.ListDTO> laundryOptionList(
            int page,
            int size,
            Boolean isActive,
            String name
    ) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<LaundryOption> laundryOptionPage;
        boolean hasName = name != null && !name.isBlank();

        if (hasName && isActive != null) {
            laundryOptionPage = repository.findByNameContainingAndIsActive(name, isActive, pageable);
        } else if (hasName) {
            laundryOptionPage = repository.findByNameContaining(name, pageable);
        } else if (isActive != null) {
            laundryOptionPage = repository.findByIsActive(isActive, pageable);
        } else {
            laundryOptionPage = repository.findAllOrderByCreatedAtDesc(pageable);
        }
        return new PageResponse<>(laundryOptionPage, LaundryOptionResponse.ListDTO::new);
    }
}
