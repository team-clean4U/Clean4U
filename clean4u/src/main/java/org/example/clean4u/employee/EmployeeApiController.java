package org.example.clean4u.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class EmployeeApiController {
    private final MailService mailService;

    @PostMapping("/api/email/send")
    public ResponseEntity<?> sendEmail(
            @RequestBody @Valid EmployeeRequest.EmailCheck emailCheck
    ) {
        mailService.sendEmail(emailCheck.getEmail());

        return ResponseEntity.ok().body(Map.of("message", "인증번호가 발송되었습니다."));
    }

    @PostMapping("/api/email/verify")
    public ResponseEntity<?> verifyEmailCode(
            @RequestBody @Valid EmployeeRequest.EmailCheck emailCheck
    ) {
        if (emailCheck.getCode() == null || emailCheck.getCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호를 입력해주세요"));
        }

        Boolean isVerified = mailService.verifyEmailCode(emailCheck.getEmail(), emailCheck.getCode());

        if (isVerified) {
            return ResponseEntity.ok().body(Map.of("message", "인증 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다."));
        }
    }
}
