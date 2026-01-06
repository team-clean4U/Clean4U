package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
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
public class AuthService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponse.JoinListDTO> joinList() {
        List<Employee> employeeList = employeeRepository.findAllByOrderByCreatedAtDesc();

        return employeeList.stream()
                .filter(e -> e.getUserStatus().equals(UserStatus.PENDING))
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

        if (!employeeEntity.getUserStatus().equals(UserStatus.PENDING)) {
            return employeeEntity;
        }

        employeeEntity.setUserStatus(UserStatus.APPROVED);
        employeeRepository.save(employeeEntity);

        return employeeEntity;
    }

    @Transactional
    public Employee reject(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        if (!employeeEntity.getUserStatus().equals(UserStatus.PENDING)) {
            return employeeEntity;
        }

        employeeEntity.setUserStatus(UserStatus.REJECTED);
        employeeRepository.save(employeeEntity);

        return employeeEntity;
    }

    @Transactional
    public void delete(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 ID의 직원을 찾을 수 없습니다."));

        employeeEntity.setActive(false);
    }

    public PageResponse<EmployeeResponse.ListDTO> getAllEmployeeWithSearch(int pageIndex, int size, String keyword, String category) {
        int validPage = Math.max(0, pageIndex);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<Employee> employeePage;

        if (keyword == null || keyword.trim().isBlank()) {
            employeePage = employeeRepository.findAllEmployee(pageable);
        } else if ("all".equalsIgnoreCase(category)) {
            employeePage = employeeRepository.searchByKeyword(keyword.trim(), pageable);
        } else if ("name".equalsIgnoreCase(category)) {
            employeePage = employeeRepository.findByNameContaining(keyword.trim(), pageable);
        } else if ("email".equalsIgnoreCase(category)) {
            employeePage = employeeRepository.findByEmailContaining(keyword.trim(), pageable);
        } else {
            employeePage = employeeRepository.findAllEmployee(pageable);
        }

        return new PageResponse<>(employeePage, EmployeeResponse.ListDTO::new);
    }
}
