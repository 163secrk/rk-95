package com.community.elderlycare.controller;

import com.community.elderlycare.model.User;
import com.community.elderlycare.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return repo.findById(id);
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<User> getByVolunteerId(@PathVariable Long volunteerId) {
        List<User> list = repo.findAll().stream()
            .filter(u -> u.getVolunteerId() != null && u.getVolunteerId().equals(volunteerId))
            .toList();
        if (list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User u) {
        if (repo.existsByUsername(u.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
        }
        User saved = repo.save(u);
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User u) {
        User existing = repo.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        u.setId(id);
        if (u.getPassword() == null || u.getPassword().isEmpty()) {
            u.setPassword(existing.getPassword());
        }
        repo.update(u);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
