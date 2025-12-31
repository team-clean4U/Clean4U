package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.example.clean4u.employee.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkScheduleService {

    private final EmployeeRepository employeeRepository;
    private final WorkScheduleRepository workScheduleRepository;

    @Transactional
    public WorkSchedule saveNormal(@Valid WorkScheduleRequest.SaveDTO saveDTO) {
        Employee employeeEntity = employeeRepository.findById(saveDTO.getEmployeeId())
                .orElseThrow(() -> new Exception404("해당 ID의 직원이 존재하지 않습니다."));

        WorkSchedule workSchedule = saveDTO.toNormalEntity(employeeEntity);
        workScheduleRepository.save(workSchedule);

        return workSchedule;
    }

    public List<EmployeeResponse.SimpleDTO> searchByName(String keyword) {

        List<Employee> employeeList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            employeeList = employeeRepository.findByNameContaining(keyword);
        } else {
            return new ArrayList<>();
        }
        return employeeList.stream()
                .map(EmployeeResponse.SimpleDTO::new)
                .collect(Collectors.toList());
    }

    public List<WorkScheduleResponse.ListDTO> scheduleList() {
        List<WorkSchedule> workSchedules = workScheduleRepository.findAll();

        return workSchedules.stream()
                .map(WorkScheduleResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public WorkScheduleResponse.DetailDTO detail(Long scheduleId) {
        WorkSchedule workScheduleEntity = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다."));

        return new WorkScheduleResponse.DetailDTO(workScheduleEntity);
    }

    public WorkScheduleResponse.UpdateDTO scheduleUpdate(Long scheduleId) {
        WorkSchedule workScheduleEntity = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다."));

        return new WorkScheduleResponse.UpdateDTO(workScheduleEntity);
    }

    @Transactional
    public WorkSchedule scheduleUpdateProc(Long scheduleId, @Valid WorkScheduleRequest.UpdateDTO updateDTO) {
        WorkSchedule workScheduleEntity = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다."));

        workScheduleEntity.update(updateDTO, workScheduleEntity.getEmployee());

        return workScheduleEntity;
    }

    @Transactional
    public void delete(Long scheduleId) {
        WorkSchedule workScheduleEntity = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception404("해당 스케줄이 존재하지 않습니다."));

        workScheduleRepository.delete(workScheduleEntity);
    }

    public PageResponse<WorkScheduleResponse.ListDTO> getAllScheduleWithSearch(
            int pageIndex, int size, String keyword, String category,
            LocalTime searchStartTime, LocalTime searchEndTime
            ) {
        int validPage = Math.max(0, pageIndex);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<WorkSchedule> schedulePage;

        if ("name".equalsIgnoreCase(category)) {
            if (keyword == null || keyword.trim().isBlank()) {
                schedulePage = workScheduleRepository.findAll(pageable);
            } else {
                schedulePage = workScheduleRepository.findByNameContaining(keyword.trim(), pageable);
            }
        } else if ("username".equalsIgnoreCase(category)) {
            if (keyword == null || keyword.trim().isBlank()) {
                schedulePage = workScheduleRepository.findAll(pageable);
            } else {
                schedulePage = workScheduleRepository.findByUsernameContaining(keyword.trim(), pageable);
            }
        } else if (searchStartTime != null && searchEndTime != null) {
            if (searchStartTime.isAfter(searchEndTime)) {
                throw new Exception400("검색 시작 시간는 검색 종료 시간보다 우선이어야 합니다.");
            }
            schedulePage = workScheduleRepository.searchByTimeRange(searchStartTime, searchEndTime, pageable);
        } else {
            schedulePage = workScheduleRepository.findAll(pageable);
        }

        return new PageResponse<>(schedulePage, WorkScheduleResponse.ListDTO::new);
    }
}
