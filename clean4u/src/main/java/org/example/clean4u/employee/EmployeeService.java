package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.review.ReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final HttpSession session;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewService reviewService;
    private final MailService mailService;

    @Value("${app.base-url}")
    private String baseUrl;

    public Employee join(@Valid EmployeeRequest.JoinDTO joinDTO, boolean emailVerified) {
        if (!emailVerified) {
            throw new Exception400("이메일 인증을 완료해 주세요");
        }

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
    public EmployeeResponse.UpdateDTO updateProc(@Valid EmployeeRequest.UpdateDTD updateDTD, Long employeeId) {
        Employee employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        if (!employeeEntity.isOwner(employeeId)) {
            throw new Exception403("회원 정보 수정 권환이 없습니다.");
        }

        String hashPsw = passwordEncoder.encode(updateDTD.getPassword());
        updateDTD.setPassword(hashPsw);
        employeeEntity.update(updateDTD);

        return new EmployeeResponse.UpdateDTO(employeeEntity);
    }

    public long pendingCount() {
        return employeeRepository.countByUserStatus(UserStatus.PENDING);
    }

    public Employee findPassword(@Valid EmployeeRequest.FindPassword findPassword) {
        Employee employeeEntity = employeeRepository.findByUsername(findPassword.getUsername())
                .orElseThrow(() -> new Exception400("해당 사용자를 찾을 수 없습니다."));

        if (employeeEntity.getEmail() == null || !employeeEntity.getEmail().equals(findPassword.getEmail())) {
            throw new Exception400("올바른 이메일이 아닙니다.");
        }

        String token = reviewService.generateShortToken();

        session.setAttribute("RESET_PASSWORD", new ResetPasswordSession(
                employeeEntity.getId(),
                token,
                LocalDateTime.now().plusMinutes(15)
        ));

        String resetLink = baseUrl + "/password/reset?token=" + token;
        mailService.sendEmailAndPassword(employeeEntity.getEmail(), resetLink);

        return employeeEntity;
    }

    @Transactional
    public Employee passwordReset(@Valid EmployeeRequest.PasswordReset passwordReset) {

        ResetPasswordSession reset = (ResetPasswordSession) session.getAttribute("RESET_PASSWORD");

        if (reset == null || reset.isExpired()) {
            throw new Exception400("세션 만료");
        }

        Employee employeeEntity = employeeRepository.findById(reset.getEmployeeId())
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        String hashPwd = passwordEncoder.encode(passwordReset.getPassword());
        employeeEntity.setPassword(hashPwd);

        return employeeEntity;
    }
}
