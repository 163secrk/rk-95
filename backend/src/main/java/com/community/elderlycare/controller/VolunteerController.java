package com.community.elderlycare.controller;

import com.community.elderlycare.model.Volunteer;
import com.community.elderlycare.repository.VolunteerRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {
    private final VolunteerRepository repo;
    public VolunteerController(VolunteerRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Volunteer> list(@RequestParam(required = false) String status) {
        if (status != null) return repo.findByStatus(status);
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Volunteer get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public Volunteer create(@Valid @RequestBody Volunteer v) { return repo.save(v); }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody Volunteer v) { v.setId(id); repo.update(v); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
