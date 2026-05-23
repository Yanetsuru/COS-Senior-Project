package com.example.demo.repository;

import com.example.demo.model.DnDSession;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DnDSessionRepository extends JpaRepository<DnDSession, Long> {
    List<DnDSession> findByUser(User user);

    List<DnDSession> findByUserOrderByLastModifiedDesc(User user);
}
