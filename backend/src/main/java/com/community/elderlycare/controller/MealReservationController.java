package com.community.elderlycare.controller;

import com.community.elderlycare.model.MealReservation;
import com.community.elderlycare.repository.MealReservationRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealReservationController {
    private final MealReservationRepository repo;
    public MealReservationController(MealReservationRepository repo) { this.repo = repo; }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private void validateMealDate(MealReservation m) {
        if (m.getMealDate() != null && !m.getMealDate().isEmpty()) {
            LocalDate mealDate = LocalDate.parse(m.getMealDate().substring(0, 10), DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            if (mealDate.isBefore(today)) {
                throw new IllegalArgumentException("用餐日期不能是过去的日期");
            }
        }
    }

    @GetMapping
    public List<MealReservation> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public MealReservation get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public MealReservation create(@Valid @RequestBody MealReservation m) {
        validateMealDate(m);
        return repo.save(m);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody MealReservation m) {
        validateMealDate(m);
        m.setId(id);
        repo.update(m);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
