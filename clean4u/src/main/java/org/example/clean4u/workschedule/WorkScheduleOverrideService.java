package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkScheduleOverrideService {

    private final EmployeeRepository employeeRepository;
    private final WorkScheduleRepository workScheduleRepository;
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
}
