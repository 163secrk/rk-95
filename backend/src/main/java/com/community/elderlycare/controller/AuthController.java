package com.community.elderlycare.controller;

import com.community.elderlycare.dto.LoginRequest;
import com.community.elderlycare.model.User;
import com.community.elderlycare.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername());
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码错误"));
        }
        if (!user.getPassword().equals(req.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码错误"));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("name", user.getName());
        result.put("volunteerId", user.getVolunteerId());

        return ResponseEntity.ok(result);
    }
}
