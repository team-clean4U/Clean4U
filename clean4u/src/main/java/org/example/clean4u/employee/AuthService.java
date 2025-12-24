package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception404;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponse.JoinListDTO> joinList() {
        List<Employee> employeeList = employeeRepository.findAllByOrderByCreatedAtDesc();

        return employeeList.stream()
                .filter(e -> e.getUserStatus().equals(UserStatus.승인대기))
                .map(EmployeeResponse.JoinListDTO::new)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse.ListDTO> list() {
        List<Employee> employeeList = employeeRepository.findAll();

        return employeeList.stream()
                .map(EmployeeResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public EmployeeResponse.DetailDTO detail(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        return new EmployeeResponse.DetailDTO(employeeEntity);

    }

    public Employee findById(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        return employeeEntity;
    }

    public List<Employee> getOverrideEmployee (Employee employee) {
        return employeeRepository.findAll().stream()
                .filter(e -> !e.getId().equals(employee.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Employee approve(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        if (!employeeEntity.getUserStatus().equals(UserStatus.승인대기)) {
            return employeeEntity;
        }

        employeeEntity.setUserStatus(UserStatus.승인);
        employeeRepository.save(employeeEntity);

        return employeeEntity;
    }

    @Transactional
    public Employee reject(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        if (!employeeEntity.getUserStatus().equals(UserStatus.승인대기)) {
            return employeeEntity;
        }

        employeeEntity.setUserStatus(UserStatus.거부);
        employeeRepository.save(employeeEntity);

        return employeeEntity;
    }

    @Transactional
    public void delete(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        employeeRepository.delete(employeeEntity);









    }
}
