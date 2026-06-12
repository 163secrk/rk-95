package com.community.elderlycare.controller;

import com.community.elderlycare.model.DispatchOrder;
import com.community.elderlycare.repository.DispatchOrderRepository;
import com.community.elderlycare.repository.VolunteerRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatches")
public class DispatchOrderController {
    private final DispatchOrderRepository repo;
    private final VolunteerRepository volunteerRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public DispatchOrderController(DispatchOrderRepository repo, VolunteerRepository volunteerRepo) {
        this.repo = repo;
        this.volunteerRepo = volunteerRepo;
    }

    @GetMapping
    public List<DispatchOrder> list(@RequestParam(required = false) Long volunteerId,
                                    @RequestParam(required = false) String status) {
        if (volunteerId != null && status != null) return repo.findByVolunteerIdAndStatus(volunteerId, status);
        if (volunteerId != null) return repo.findByVolunteerId(volunteerId);
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public DispatchOrder get(@PathVariable Long id) { return repo.findById(id); }

    @PostMapping
    public DispatchOrder create(@RequestBody DispatchOrder d) { return repo.save(d); }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody DispatchOrder d) {
        d.setId(id);
        repo.update(d);
        if (d.getVolunteerId() != null && "已派单".equals(d.getStatus())) {
            var v = volunteerRepo.findById(d.getVolunteerId());
            v.setStatus("服务中");
            volunteerRepo.update(v);
        }
        if ("已完成".equals(d.getStatus()) && d.getVolunteerId() != null) {
            long activeCount = repo.countActiveByVolunteerId(d.getVolunteerId(), id);
            if (activeCount == 0) {
                var v = volunteerRepo.findById(d.getVolunteerId());
                v.setStatus("空闲");
                volunteerRepo.update(v);
            }
        }
    }

    @PostMapping("/{id}/start")
    public DispatchOrder startService(@PathVariable Long id) {
        DispatchOrder order = repo.findById(id);
        if (order == null) {
            throw new RuntimeException("派单不存在");
        }
        String now = LocalDateTime.now().format(formatter);
        order.setServiceStartTime(now);
        order.setStatus("服务中");
        repo.update(order);
        if (order.getVolunteerId() != null) {
            var v = volunteerRepo.findById(order.getVolunteerId());
            v.setStatus("服务中");
            volunteerRepo.update(v);
        }
        return repo.findById(id);
    }

    @PostMapping("/{id}/complete")
    public DispatchOrder completeService(@PathVariable Long id) {
        DispatchOrder order = repo.findById(id);
        if (order == null) {
            throw new RuntimeException("派单不存在");
        }
        String now = LocalDateTime.now().format(formatter);
        order.setServiceEndTime(now);
        if (order.getServiceStartTime() != null) {
            LocalDateTime start = LocalDateTime.parse(order.getServiceStartTime(), formatter);
            LocalDateTime end = LocalDateTime.parse(now, formatter);
            long minutes = Duration.between(start, end).toMinutes();
            order.setActualDuration((int) minutes);
        }
        order.setStatus("已完成");
        repo.update(order);
        if (order.getVolunteerId() != null) {
            long activeCount = repo.countActiveByVolunteerId(order.getVolunteerId(), id);
            if (activeCount == 0) {
                var v = volunteerRepo.findById(order.getVolunteerId());
                v.setStatus("空闲");
                volunteerRepo.update(v);
            }
        }
        return repo.findById(id);
    }

    @PostMapping("/{id}/review")
    public DispatchOrder reviewService(@PathVariable Long id, @RequestBody Map<String, Object> review) {
        DispatchOrder order = repo.findById(id);
        if (order == null) {
            throw new RuntimeException("派单不存在");
        }
        if (review.containsKey("rating")) {
            order.setRating((Integer) review.get("rating"));
        }
        if (review.containsKey("comment")) {
            order.setReviewComment((String) review.get("comment"));
        }
        repo.update(order);
        return repo.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
