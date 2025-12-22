package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.example.clean4u.employee.EmployeeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkScheduleService {

    private final EmployeeRepository employeeRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleOverrideRepository workScheduleOverrideRepository;

    @Transactional
    public WorkSchedule saveNormal(WorkScheduleRequest.SaveDTO saveDTO) {
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
    public WorkSchedule scheduleUpdateProc(Long scheduleId, WorkScheduleRequest.UpdateDTO updateDTO) {
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
}
