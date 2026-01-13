package org.example.clean4u.employee;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResetPasswordSession {
    private Long employeeId;
    private String token;
    private LocalDateTime expireAt;

    public ResetPasswordSession(Long employeeId, String token, LocalDateTime expireAt) {
        this.employeeId = employeeId;
        this.token = token;
        this.expireAt = expireAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }
}
