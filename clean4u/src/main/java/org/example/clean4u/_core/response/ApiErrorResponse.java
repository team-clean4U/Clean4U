package org.example.clean4u._core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
}
