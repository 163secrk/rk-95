package com.community.elderlycare.controller;

import com.community.elderlycare.model.MealReservation;
import com.community.elderlycare.repository.MealReservationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealReservationController {
    private final MealReservationRepository repo;
    public MealReservationController(MealReservationRepository repo) { this.repo = repo; }

    @GetMapping
    public List<MealReservation> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public MealReservation get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public MealReservation create(@RequestBody MealReservation m) { return repo.save(m); }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody MealReservation m) { m.setId(id); repo.update(m); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
