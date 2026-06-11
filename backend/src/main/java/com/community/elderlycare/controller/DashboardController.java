package com.community.elderlycare.controller;

import com.community.elderlycare.model.DashboardStats;
import com.community.elderlycare.repository.ElderRepository;
import com.community.elderlycare.repository.MealReservationRepository;
import com.community.elderlycare.repository.HealthRecordRepository;
import com.community.elderlycare.repository.DispatchOrderRepository;
import com.community.elderlycare.repository.VolunteerRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ElderRepository elderRepo;
    private final MealReservationRepository mealRepo;
    private final HealthRecordRepository healthRepo;
    private final DispatchOrderRepository dispatchRepo;
    private final VolunteerRepository volunteerRepo;

    public DashboardController(ElderRepository elderRepo, MealReservationRepository mealRepo,
                               HealthRecordRepository healthRepo, DispatchOrderRepository dispatchRepo,
                               VolunteerRepository volunteerRepo) {
        this.elderRepo = elderRepo;
        this.mealRepo = mealRepo;
        this.healthRepo = healthRepo;
        this.dispatchRepo = dispatchRepo;
        this.volunteerRepo = volunteerRepo;
    }

    @GetMapping("/stats")
    public DashboardStats stats() {
        DashboardStats s = new DashboardStats();
        s.setElderCount(elderRepo.count());
        s.setTodayMealCount(mealRepo.countToday());
        s.setTodayHealthCount(healthRepo.countToday());
        s.setPendingDispatchCount(dispatchRepo.countPending());
        s.setVolunteerCount(volunteerRepo.count());
        return s;
    }
}
