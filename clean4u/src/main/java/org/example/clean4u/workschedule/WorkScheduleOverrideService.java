package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkScheduleOverrideService {

    private final EmployeeRepository employeeRepository;
    private final WorkScheduleOverrideRepository workScheduleOverrideRepository;

    @Transactional
    public WorkScheduleOverride saveOverride(WorkScheduleRequest.SaveDTO saveDTO) {
        Employee originalEntity = employeeRepository.findById(saveDTO.getEmployeeId())
                .orElseThrow(() -> new Exception404("해당 ID의 직원이 존재하지 않습니다."));

        Employee overrideEntity = employeeRepository.findById(saveDTO.getOverrideId())
                .orElseThrow(() -> new Exception404("해당 ID의 직원이 존재하지 않습니다."));

        WorkScheduleOverride workScheduleOverride = saveDTO.toSickEntity(originalEntity, overrideEntity);
        workScheduleOverrideRepository.save(workScheduleOverride);

        return workScheduleOverride;
    }

    public List<WorkScheduleOverrideResponse.ListDTO> overrideList() {
        List<WorkScheduleOverride> workScheduleOverrides = workScheduleOverrideRepository.findAll();

        return workScheduleOverrides.stream()
                .map(WorkScheduleOverrideResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public WorkScheduleOverrideResponse.DetailDTO overrideDetail(Long scheduleId) {
        WorkScheduleOverride workScheduleOverrideEntity = workScheduleOverrideRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다."));

        return new WorkScheduleOverrideResponse.DetailDTO(workScheduleOverrideEntity);
    }

    public WorkScheduleOverrideResponse.UpdateDTO overrideUpdateForm(Long scheduleId) {
        WorkScheduleOverride workScheduleOverrideEntity = workScheduleOverrideRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다."));

        return new WorkScheduleOverrideResponse.UpdateDTO(workScheduleOverrideEntity);
    }

    @Transactional
    public WorkScheduleOverride overrideUpdateProc(Long scheduleId, WorkScheduleOverrideRequest.UpdateDTO updateDTO) {
        WorkScheduleOverride workScheduleOverrideEntity = workScheduleOverrideRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다"));

        Employee originalEntity = employeeRepository.findById(updateDTO.getOriginalId())
                .orElseThrow(() -> new Exception404("해당 ID의 직원이 존재하지 않습니다."));

        Employee overrideEntity = employeeRepository.findById(updateDTO.getOverrideId())
                .orElseThrow(() -> new Exception404("해당 ID의 직원이 존재하지 않습니다."));

        workScheduleOverrideEntity.update(updateDTO, originalEntity, overrideEntity);

        return workScheduleOverrideEntity;
    }

    @Transactional
    public void delete(Long scheduleId) {
        WorkScheduleOverride workScheduleOverrideEntity = workScheduleOverrideRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다"));

        workScheduleOverrideRepository.delete(workScheduleOverrideEntity);
    }

    public PageResponse<WorkScheduleOverrideResponse.ListDTO> getAllScheduleWithSearch(
            int pageIndex, int size, String keyword, String category,
            LocalTime startTime, LocalTime endTime, LocalDate date
    ) {
        int validPage = Math.max(0, pageIndex);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<WorkScheduleOverride> schedulePage;

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new Exception400("검색 시작 시간는 검색 종료 시간보다 우선이어야 합니다.");
            }
            schedulePage = workScheduleOverrideRepository.searchByTimeRange(startTime, endTime, pageable);
        } else if ("originalName".equalsIgnoreCase(category)) {
            if (keyword == null || keyword.trim().isBlank()) {
                schedulePage = workScheduleOverrideRepository.findAll(pageable);
            } else {
                schedulePage = workScheduleOverrideRepository.findByOriginalEmployeeNameContaining(keyword.trim(), pageable);
            }
        } else if ("overrideName".equalsIgnoreCase(category)) {
            if (keyword == null || keyword.trim().isBlank()) {
                schedulePage = workScheduleOverrideRepository.findAll(pageable);
            } else {
                schedulePage = workScheduleOverrideRepository.findByOverrideEmployeeNameContaining(keyword.trim(), pageable);
            }
        } else {
            schedulePage = workScheduleOverrideRepository.findAll(pageable);
        }

        return new PageResponse<>(schedulePage, WorkScheduleOverrideResponse.ListDTO::new);
    }
}
