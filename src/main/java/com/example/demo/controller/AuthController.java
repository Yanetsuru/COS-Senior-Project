package com.example.demo.controller;
import com.example.demo.model.DnDSession;
import com.example.demo.repository.DnDSessionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.ui.Model;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DnDSessionRepository dnDSessionRepository;

    @PostMapping("AIDnD/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model error, Model model) {
        Optional<User> optionalUser = userRepository.findByUsername(username);


        if (optionalUser.isEmpty()) {
            error.addAttribute("error", "User not found");
            return "login"; // returns to login page without crashing
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            error.addAttribute("error", "Incorrect password");
            return "login";
        }
        session.setAttribute("user", user);
        model.addAttribute("user", user);
        // Fetch all DnDSessions for this user
        List<DnDSession> sessions = dnDSessionRepository.findByUser(user);
        model.addAttribute("sessions", sessions);
        return "sessions"; // successful login

    }

    @PostMapping("AIDnD/register")
    public String register(@RequestParam String username, @RequestParam String password, HttpSession session, Model error) {
        if (userRepository.findByUsername(username).isPresent()) {
            error.addAttribute("error", "Username already exists");
            return "register";
        }
        User user = new User();
        user.setUsername(username);
        String hashed = passwordEncoder.encode(password);
        user.setPasswordHash(hashed);
        userRepository.save(user);
        session.setAttribute("user", user);

        return "redirect:/AIDnD/create";
    }
    @GetMapping("AIDnD/register")
    public String showRegister() {
        return "register";
    }
    @GetMapping("AIDnD/login")
    public String showLogin() {
        return "login";
    }
}
