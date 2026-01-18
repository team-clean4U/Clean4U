package org.example.clean4u.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.clean4u.employee.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class NoticeRequest {
    @Data
    public static class SaveDTO {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 2000, message = "내용은 2000자 이내여야 합니다.")
        private String content;
        private List<MultipartFile> attachments = new ArrayList<>();

        public Notice toEntity(Employee employee) {
            Notice notice = Notice.builder()
                    .title(this.title)
                    .content(this.content)
                    .employee(employee)
                    .build();

            return notice;
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 2000, message = "내용은 2000자 이내여야 합니다.")
        private String content;
        private List<Long> deleteFileIds = new ArrayList<>();
        private List<MultipartFile> newAttachments = new ArrayList<>();

    }

    @Data
    public static class ImageUploadDTO {
        List<MultipartFile> uploadImages;
    }
}
