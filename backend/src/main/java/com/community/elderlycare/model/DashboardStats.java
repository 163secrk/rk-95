package com.community.elderlycare.model;

import lombok.Data;

@Data
public class DashboardStats {
    private long elderCount;
    private long todayMealCount;
    private long todayHealthCount;
    private long pendingDispatchCount;
    private long volunteerCount;
}
