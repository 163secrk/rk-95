package com.community.elderlycare.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DispatchOrder {
    private Long id;
    private Long elderId;
    private Long volunteerId;
    private String serviceType;
    private String serviceDate;
    private String description;
    private String status;
    private String serviceStartTime;
    private String serviceEndTime;
    private Integer actualDuration;
    private Integer rating;
    private String reviewComment;
    private LocalDateTime createdAt;
    private String elderName;
    private String volunteerName;
}
