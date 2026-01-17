package org.example.clean4u.notice;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "notice_file")
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
}

