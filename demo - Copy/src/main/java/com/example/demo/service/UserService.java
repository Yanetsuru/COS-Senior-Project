package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private static UserRepository userRepository;

    public static User Login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }
    public User register(String username, String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hash);
        return userRepository.save(user);
    }
}
