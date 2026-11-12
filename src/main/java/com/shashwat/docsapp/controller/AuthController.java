package com.shashwat.docsapp.controller;

import com.shashwat.docsapp.dto.LoginRequest;
import com.shashwat.docsapp.model.User;
import com.shashwat.docsapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        User user = authService.login(req.getUsername(), req.getPassword(), session);
        return userToMap(user);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        authService.logout(session);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Logged out");
        return res;
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {
        return userToMap(authService.getCurrentUser(session));
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", user.getId());
        m.put("username", user.getUsername());
        return m;
    }
}