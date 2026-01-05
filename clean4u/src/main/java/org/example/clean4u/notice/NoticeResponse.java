package org.example.clean4u.notice;

import lombok.Data;
import org.example.clean4u._core.utils.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class NoticeResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String writer;
        private String createdAt;

        public ListDTO(Notice notice) {
            this.id = notice.getId();
            this.title = notice.getTitle();
            if (notice.getEmployee() != null) {
                this.writer = notice.getEmployee().getName();
            }
            if (notice.getCreatedAt() != null) {
                this.createdAt = DateUtil.timestampFormat(notice.getCreatedAt());
            }
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String writer;
        private String content;
        private String createdAt;
        private List<String> uploadImages;

        public DetailDTO(Notice notice){
            this.id = notice.getId();
            this.title = notice.getTitle();
            if (notice.getEmployee() != null) {
                this.writer = notice.getEmployee().getName();
            }
            this.content = notice.getContent();
            if (notice.getCreatedAt() != null) {
                this.createdAt = DateUtil.timestampFormat(notice.getCreatedAt());
            }
            this.uploadImages = notice.getNoticeImagePath();
        }
    }
}
