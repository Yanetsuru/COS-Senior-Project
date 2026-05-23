package com.example.demo.controller;
import com.example.demo.model.Character;
import com.example.demo.model.DnDSession;
import com.example.demo.model.SessionHistory;
import com.example.demo.model.User;
import com.example.demo.repository.CharacterRepository;
import com.example.demo.repository.DnDSessionRepository;
import com.example.demo.repository.SessionHistoryRepository;
import com.example.demo.service.CallOpenAIService;
import com.example.demo.service.CharacterFactory;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("character") // keep character between pages
public class MainController {

    @Autowired
    ChatMemoryRepository chatMemoryRepository;
    @Autowired
    CallOpenAIService callOpenAIService;
    @Autowired
    private DnDSessionRepository sessionRepository;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private SessionHistoryRepository sessionHistoryRepository;

    // === MAIN MENU ===
    @GetMapping("/AIDnD")
    public String showMainMenu() {
        return "main-menu";
    }

    // === CHARACTER FORM ===
    @GetMapping("/AIDnD/create")
    public String showCharacterForm(Model model) {

        model.addAttribute("character", CharacterFactory.createDefaultCharacter());
        return "character-form";
    }

    // === HANDLE FORM SUBMISSION ===
    @PostMapping("/AIDnD/create")
    public String submitCharacterForm(@ModelAttribute("character") Character character, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/AIDnD/login";
        }
        session.setAttribute("character", character); // save in session
        Character savedCharacter = characterRepository.save(character);
        // Create new session
        DnDSession dndSession = new DnDSession();
        dndSession.setName(savedCharacter.getName() + "'s Adventure");
        dndSession.setUser(user);
        dndSession.setCharacter(savedCharacter);
        dndSession.setLastModified(LocalDateTime.now());
        DnDSession savedSession = sessionRepository.save(dndSession);

        session.setAttribute("activeSession", savedSession);
        return "redirect:/AIDnD/chat"; // redirect to chat GET
    }

    // === CHAT PAGE ===
    @GetMapping("/AIDnD/chat")
    public String showChat(@ModelAttribute("character") Character character, HttpSession httpSession, Model model) {


        httpSession.setAttribute("character", character);  // save character for websocket
        if (character == null) {
            // Redirect to character creation page
            return "redirect:/AIDnD/create";
        }
        model.addAttribute("character", character);
        model.addAttribute("character", httpSession.getAttribute("character"));
        return "chat";
    }

    @PostMapping("/AIDnD/chat/send")
    public ResponseEntity<?> getAiResponse(@RequestBody Map<String, Object> request) throws InterruptedException {
        List<Map<String,String>> input = new ArrayList<>();
        input.add(Map.of("role", "user", "content", (String)request.get("message")));

        String aiResponse = callOpenAIService.callOpenAiApi(input);

        // Save AI response
        SessionHistory aiMessage = new SessionHistory() {

        };

        //Save AI Response
        aiMessage.setContent(aiResponse);
        aiMessage.setTimestamp(LocalDateTime.now());
        aiMessage.setRole("ai");


        Map<String, Object> response = new HashMap<>();
        response.put("content", aiResponse);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/AIDnD/chat/send")
    public String sendChatGetFallback() {
        return "redirect:/AIDnD/chat";
    }





}
