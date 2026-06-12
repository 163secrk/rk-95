package com.community.elderlycare.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthRecord {
    private Long id;
    @NotNull(message = "请选择老人")
    private Long elderId;
    @NotBlank(message = "请选择登记日期")
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
