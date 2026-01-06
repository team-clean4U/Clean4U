package org.example.clean4u.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public Employee join(@Valid EmployeeRequest.JoinDTO joinDTO) {
        if (employeeRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            throw new Exception400("이미 존재하는 사용자 입니다.");
        }

        String hashPwd = passwordEncoder.encode(joinDTO.getPassword());
        Employee employee = joinDTO.toEntity();
        employee.setPassword(hashPwd);

        return employeeRepository.save(employee);
    }

    public Employee login(@Valid EmployeeRequest.LoginDTO loginDTO) {
        Employee employeeEntity = employeeRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다."));

        if (employeeEntity == null) {
            throw new Exception404("회원가입 되지않은 사용자입니다.");
        }

        if (!employeeEntity.getUserStatus().equals(UserStatus.APPROVED)) {
            throw new Exception403("회원가입 승인되지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), employeeEntity.getPassword())) {
            throw new Exception400("사용자명 혹은 비밀번호가 올바르지 않습니다.");
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

        String hashPsw = passwordEncoder.encode(updateDTD.getPassword());
        updateDTD.setPassword(hashPsw);
        employeeEntity.update(updateDTD);

        return employeeEntity;
    }

    public long pendingCount() {
        return employeeRepository.countByUserStatus(UserStatus.PENDING);
    }
}
