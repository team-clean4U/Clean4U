package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                DayOfWeek.MONDAY, 1,
                DayOfWeek.TUESDAY, 2,
                DayOfWeek.WEDNESDAY, 3,
                DayOfWeek.THURSDAY, 4,
                DayOfWeek.FRIDAY, 5,
                DayOfWeek.SATURDAY, 6,
                DayOfWeek.SUNDAY, 7
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
}
