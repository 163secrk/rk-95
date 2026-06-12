package com.community.elderlycare.controller;

import com.community.elderlycare.model.HealthRecord;
import com.community.elderlycare.repository.HealthRecordRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/health")
public class HealthRecordController {
    private final HealthRecordRepository repo;
    public HealthRecordController(HealthRecordRepository repo) { this.repo = repo; }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private void validateRecordDate(HealthRecord h) {
        if (h.getRecordDate() != null && !h.getRecordDate().isEmpty()) {
            LocalDate recordDate = LocalDate.parse(h.getRecordDate().substring(0, 10), DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            if (recordDate.isBefore(today)) {
                throw new IllegalArgumentException("登记日期不能是过去的日期");
            }
        }
    }

    private void validateBloodPressure(HealthRecord h) {
        if (h.getBloodPressure() != null && !h.getBloodPressure().isEmpty()) {
            String bp = h.getBloodPressure();
            Pattern pattern = Pattern.compile("(-?\\d+)\\s*/\\s*(-?\\d+)");
            Matcher matcher = pattern.matcher(bp);
            if (matcher.find()) {
                int systolic = Integer.parseInt(matcher.group(1));
                int diastolic = Integer.parseInt(matcher.group(2));
                if (systolic < 0 || diastolic < 0) {
                    throw new IllegalArgumentException("血压不能为负数");
                }
            }
        }
    }

    @GetMapping
    public List<HealthRecord> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public HealthRecord get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public HealthRecord create(@Valid @RequestBody HealthRecord h) {
        validateRecordDate(h);
        validateBloodPressure(h);
        return repo.save(h);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody HealthRecord h) {
        validateRecordDate(h);
        validateBloodPressure(h);
        h.setId(id);
        repo.update(h);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
