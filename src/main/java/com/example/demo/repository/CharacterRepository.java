package com.example.demo.repository;

import com.example.demo.model.DnDSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<com.example.demo.model.Character, Long> {
}
