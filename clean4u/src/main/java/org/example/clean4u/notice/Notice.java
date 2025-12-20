package org.example.clean4u.notice;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.time.BaseTimeEntity;

@Data
@NoArgsConstructor
@Table(name = "notice_tb")
@Entity
public class Notice extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_tb", nullable = false)
    private Employee employee;

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
}
