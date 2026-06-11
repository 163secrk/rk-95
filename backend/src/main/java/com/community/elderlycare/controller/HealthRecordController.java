package com.community.elderlycare.controller;

import com.community.elderlycare.model.HealthRecord;
import com.community.elderlycare.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthRecordController {
    private final HealthRecordRepository repo;
    public HealthRecordController(HealthRecordRepository repo) { this.repo = repo; }

    @GetMapping
    public List<HealthRecord> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public HealthRecord get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public HealthRecord create(@RequestBody HealthRecord h) { return repo.save(h); }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody HealthRecord h) { h.setId(id); repo.update(h); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
