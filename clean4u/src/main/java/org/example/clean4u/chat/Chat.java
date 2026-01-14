package org.example.clean4u.chat;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Table(name = "chat_tb")
@Entity
public class Chat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Chat(String message, Employee employee) {
        this.message = message;
        this.employee = employee;
    }
}
