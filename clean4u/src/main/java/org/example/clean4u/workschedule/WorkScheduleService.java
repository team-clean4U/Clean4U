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

    @Transactional
    public WorkSchedule saveProc(WorkScheduleRequest.SaveDTO saveDTO) {
        Employee employeeEntity = employeeRepository.findById(saveDTO.getEmployeeId())
                .orElseThrow(() -> new Exception404("해당 ID의 직원이 존재하지 않습니다."));

        WorkSchedule workSchedule = saveDTO.toEntity(employeeEntity);
        workScheduleRepository.save(workSchedule);

        return workSchedule;
    }

    public List<WorkScheduleResponse.ListDTO> scheduleList() {
        List<WorkSchedule> workScheduleList = workScheduleRepository.findAll();

        Map<DayOfWeek, Integer> list = Map.of(
                DayOfWeek.월요일, 1,
                DayOfWeek.화요일, 2,
                DayOfWeek.수요일, 3,
                DayOfWeek.목요일, 4,
                DayOfWeek.금요일, 5,
                DayOfWeek.토요일, 6,
                DayOfWeek.일요일, 7
        );

        workScheduleList.sort(Comparator.comparing(ws -> ws.getDays().stream()
                .mapToInt(list::get)
                .min()
                .orElse(7)
        ));

        return workScheduleList.stream()
                .map(WorkScheduleResponse.ListDTO::new)
                .collect(Collectors.toList());
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
}
