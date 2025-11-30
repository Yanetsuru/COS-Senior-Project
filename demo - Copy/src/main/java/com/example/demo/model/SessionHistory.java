package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SessionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;     // "user" or "assistant"

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private DnDSession session;

    public SessionHistory() {}

    public SessionHistory(String role, String content, DnDSession session) {
        this.role = role;
        this.content = content;
        this.session = session;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DnDSession getSession() {
        return session;
    }

    public void setSession(DnDSession session) {
        this.session = session;
    }
}
