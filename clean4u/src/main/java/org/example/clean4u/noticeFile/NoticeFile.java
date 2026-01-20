package org.example.clean4u.noticeFile;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.notice.Notice;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

@Entity
@Table(name = "notice_file_tb")
@Data
@NoArgsConstructor
public class NoticeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notice_notice_file"))
    private Notice notice;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    private String contentType; // 브라우저에서 바로 열릴지 / 다운로드 될지 결정 (MIME)
    private Long fileSize; // 파일 저장 시점의 크기 기록
    private String filePath; // 저장 경로

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public NoticeFile(Long id,
                      Notice notice,
                      String originalName,
                      String storedName,
                      String contentType,
                      Long fileSize,
                      String filePath,
                      Timestamp createdAt
    ) {
        this.id = id;
        this.notice = notice;
        this.originalName = originalName;
        this.storedName = storedName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    public static NoticeFile createNoticeFile(MultipartFile file, String storedName, String filePath) {
        Path fullPath = Paths.get(filePath).resolve(storedName);

        return NoticeFile.builder()
                .originalName(file.getOriginalFilename())
                .storedName(storedName)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(fullPath.toString())
                .build();
    }
}

