package org.example.clean4u.laundryOption;

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

    public List<LaundryOptionResponse.ListDTO> findByIsActive(Boolean isActive) {
        List<LaundryOption> laundryOptionList = repository.findByIsActive(isActive);
        return laundryOptionList.stream()
                .map(LaundryOptionResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public List<LaundryOptionResponse.ListDTO> findByNameContaining(String name) {
        List<LaundryOption> laundryOptionList = repository.findByNameContaining(name);
        return laundryOptionList.stream()
                .map(LaundryOptionResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public List<LaundryOptionResponse.ListDTO> findByNameContainingAndIsActive(String name, Boolean isActive) {
        List<LaundryOption> laundryOptionList = repository.findByNameContainingAndIsActive(name, isActive);
        return laundryOptionList.stream()
                .map(LaundryOptionResponse.ListDTO::new)
                .collect(Collectors.toList());
    }
}
