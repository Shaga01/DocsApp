package com.shashwat.docsapp.service;

import com.shashwat.docsapp.exception.UnauthorizedException;
import com.shashwat.docsapp.model.User;
import com.shashwat.docsapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String SESSION_KEY = "userId";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password, HttpSession session) {
        User user = userRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedException("Invalid username or password");
        }

        session.setAttribute(SESSION_KEY, user.getId());
        return user;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        Object id = session.getAttribute(SESSION_KEY);
        if (id == null) {
            throw new UnauthorizedException("Please log in first");
        }
        return userRepository.findById((Long) id)
                .orElseThrow(() -> new UnauthorizedException("Please log in first"));
    }
}