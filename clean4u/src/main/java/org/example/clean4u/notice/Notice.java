package org.example.clean4u.notice;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Table(name = "notice_tb")
@Entity
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeFile> noticeFiles = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public Notice(String title, String content, Employee employee) {
        this.title = title;
        this.content = content;
        this.employee = employee;
    }

    public void update(NoticeRequest.UpdateDTO updateDTO) {
        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();
    }

    public void addNoticeFile(NoticeFile noticeFile) {
        if (noticeFile == null) return;

        this.noticeFiles.add(noticeFile);
        noticeFile.setNotice(this);
    }

    public void addNoticeFiles(List<NoticeFile> files) {
        if (files == null || files.isEmpty()) { return; }
        for (NoticeFile file: files) {
            addNoticeFile(file);
        }
    }

    public void clearFiles() {
        this.noticeFiles.clear();
    }

    public void removeFiles(List<NoticeFile> files) {
        if (files == null || files.isEmpty()) return;
        this.noticeFiles.removeAll(files);
    }

}
