package com.example.demo.controller;
import com.example.demo.model.Character;
import com.example.demo.repository.CharacterRepository;
import com.example.demo.repository.SessionHistoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.demo.model.DnDSession;
import com.example.demo.model.User;
import com.example.demo.repository.DnDSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class SessionController {


    @Autowired
    private DnDSessionRepository sessionRepository;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private SessionHistoryRepository sessionHistoryRepository;


    public SessionController(DnDSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/AIDnd/sessions")
    public String listSessions(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/AIDnD/login";

        List<DnDSession> sessions = sessionRepository.findByUserOrderByLastModifiedDesc(user);

        model.addAttribute("sessions", sessions);

        return "sessions";
    }

    @GetMapping("/AIDnD/session/{id}")
    public String continueSession(@PathVariable Long id, HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/AIDnD/login";

        DnDSession dndSession = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Prevent users from accessing each other's sessions
        if (!dndSession.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        // Attach session + character to WebSocket
        session.setAttribute("activeSession", dndSession);
        session.setAttribute("character", dndSession.getCharacter());
        model.addAttribute("session", dndSession);
        model.addAttribute("character", dndSession.getCharacter());
        return "chat";
    }
    @GetMapping("/AIDnD/api/session/{id}/messages")
    @ResponseBody
    public List<Map<String, Object>> getSessionMessages(@PathVariable Long id, HttpSession httpSession) {

        User user = (User) httpSession.getAttribute("user");
        if (user == null)
            throw new RuntimeException("Not logged in");

        DnDSession dndSession = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!dndSession.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Not allowed");

        return dndSession.getMessages().stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("role", m.getRole());
                    map.put("content", m.getContent());
                    map.put("timestamp", m.getTimestamp().toString());
                    return map;
                })
                .toList();
    }
}


