package com.community.elderlycare.controller;

import com.community.elderlycare.model.DispatchOrder;
import com.community.elderlycare.repository.DispatchOrderRepository;
import com.community.elderlycare.repository.VolunteerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatches")
public class DispatchOrderController {
    private final DispatchOrderRepository repo;
    private final VolunteerRepository volunteerRepo;
    public DispatchOrderController(DispatchOrderRepository repo, VolunteerRepository volunteerRepo) {
        this.repo = repo;
        this.volunteerRepo = volunteerRepo;
    }

    @GetMapping
    public List<DispatchOrder> list() { return repo.findAll(); }

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
            var v = volunteerRepo.findById(d.getVolunteerId());
            v.setStatus("空闲");
            volunteerRepo.update(v);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
