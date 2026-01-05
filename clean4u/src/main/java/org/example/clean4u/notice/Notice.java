package org.example.clean4u.notice;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "notice_image_tb",
            joinColumns = @JoinColumn(name = "notice_id")
    )
    @Column(name = "image_name")
    private List<String> noticeImages = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public Notice(String title, String content, Employee employee, List<String> noticeImages) {
        this.title = title;
        this.content = content;
        this.employee = employee;
        this.noticeImages = noticeImages;
    }

    public void update(NoticeRequest.UpdateDTO updateDTO) {
        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();
    }

    public void clearImages() {
        this.noticeImages.clear();
    }

    public void addImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            this.noticeImages.addAll(images);
        }
    }

    public List<String> getNoticeImagePath() {
        if (this.noticeImages == null || this.noticeImages.isEmpty()) {
            return List.of();
        }

        List<String> noticeImagePaths = new ArrayList<>();

        for (String image: this.noticeImages) {
            if (image == null) {
                continue;
            }

            if (image.startsWith("http")) {
                noticeImagePaths.add(image);
            } else {
                noticeImagePaths.add("/images/notice" + image);
            }
        }

        return noticeImagePaths;
    }
}
