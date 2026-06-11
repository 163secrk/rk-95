package com.community.elderlycare.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthRecord {
    private Long id;
    private Long elderId;
    private String recordDate;
    private String bloodPressure;
    private Integer heartRate;
    private String bloodSugar;
    private String temperature;
    private String medication;
    private String notes;
    private LocalDateTime createdAt;
    private String elderName;
}
