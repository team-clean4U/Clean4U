package org.example.clean4u.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception400;
import org.example.clean4u._core.exception.Exception403;
import org.example.clean4u._core.exception.Exception404;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Employee join(@Valid EmployeeRequest.JoinDTO joinDTO) {
        if (employeeRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            throw new Exception400("이미 존재하는 사용자 입니다.");
        }

        Employee employee = joinDTO.toEntity();
        return employeeRepository.save(employee);
    }

    public Employee login(@Valid EmployeeRequest.LoginDTO loginDTO) {
        Employee employeeEntity = employeeRepository.findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다."));

        if (!employeeEntity.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new Exception404("회원가입 되어있지 않는 사용자입니다.");
        }

        return employeeEntity;
    }

    public Employee update(Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        if (!employeeEntity.isOwner(employeeId)) {
            throw new Exception403("회원 정보 수정 권환이 없습니다.");
        }

        return employeeEntity;
    }

    @Transactional
    public Employee updateProc(@Valid EmployeeRequest.UpdateDTD updateDTD, Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        if (!employeeEntity.isOwner(employeeId)) {
            throw new Exception403("회원 정보 수정 권환이 없습니다.");
        }

        employeeEntity.update(updateDTD);

        return employeeEntity;
    }
}
