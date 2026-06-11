package com.community.elderlycare.controller;

import com.community.elderlycare.model.Elder;
import com.community.elderlycare.repository.ElderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elders")
public class ElderController {
    private final ElderRepository repo;
    public ElderController(ElderRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Elder> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Elder get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public Elder create(@RequestBody Elder e) { return repo.save(e); }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Elder e) { e.setId(id); repo.update(e); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
