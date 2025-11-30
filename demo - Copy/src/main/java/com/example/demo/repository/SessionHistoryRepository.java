package com.example.demo.repository;

import com.example.demo.model.DnDSession;
import com.example.demo.model.SessionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionHistoryRepository extends JpaRepository<SessionHistory, Long> {
    List<SessionHistory> findBySessionOrderByTimestampAsc(DnDSession session);
}
