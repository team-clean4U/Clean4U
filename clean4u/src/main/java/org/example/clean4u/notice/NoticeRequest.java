package org.example.clean4u.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.clean4u.employee.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class NoticeRequest {
    @Data
    public static class SaveDTO {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 1000, message = "내용은 1000자 이내여야 합니다.")
        private String content;
        private List<MultipartFile> noticeImages;

        public Notice toEntity(Employee employee, List<String> noticeImageNames) {
            Notice notice = Notice.builder()
                    .title(this.title)
                    .content(this.content)
                    .employee(employee)
                    .build();

            if (noticeImageNames != null && !noticeImageNames.isEmpty()) {
                notice.addImages(noticeImageNames);
            }
            return notice;
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
        private List<MultipartFile> noticeImages;
        private List<String> noticeImageNames;
    }

}
