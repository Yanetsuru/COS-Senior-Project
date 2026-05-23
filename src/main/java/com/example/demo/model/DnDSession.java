package com.example.demo.model;

import com.example.demo.repository.DnDSessionRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.model.Character;
@Entity
public class DnDSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    @ManyToOne
    private User user;
    @OneToOne(cascade = CascadeType.ALL)
    private Character character;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionHistory> messages = new ArrayList<>();


    private LocalDateTime lastModified;
    public DnDSession() {
        this.lastModified = LocalDateTime.now();
    }

    public void saveMessage(DnDSession session, String sender, String content) {
        SessionHistory msg = new SessionHistory();
        msg.setSession(this); // associate with this session
        msg.setRole(sender);
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());

        messages.add(msg);
        lastModified = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SessionHistory> getMessages() {
        return messages;
    }

    public void setMessages(List<SessionHistory> messages) {
        this.messages = messages;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
