package org.example.clean4u.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.clean4u.employee.Employee;

public class NoticeRequest {
    @Data
    public static class SaveDTO {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 1000, message = "내용은 1000자 이내여야 합니다.")
        private String content;

        public Notice toEntity(Employee employee) {
            return new Notice(title, content, employee);
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 1000, message = "내용은 1000자 이내여야 합니다.")
        private String content;
    }

}
