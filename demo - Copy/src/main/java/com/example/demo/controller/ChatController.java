package com.example.demo.controller;


import com.example.demo.model.DnDSession;
import com.example.demo.repository.DnDSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
@Controller
public class ChatController {

    @Autowired
    private DnDSessionRepository sessionRepository;

    @GetMapping("/AIDnD/chat/{id}")
    public String loadSession(@PathVariable Long id, Model model, HttpSession httpSession) {
        DnDSession session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("session", session);
        httpSession.setAttribute("currentSession", session); // for WebSocket
        return "chat";
    }
}

