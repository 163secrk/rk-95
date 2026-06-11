package com.community.elderlycare.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MealReservation {
    private Long id;
    private Long elderId;
    private String mealDate;
    private String mealType;
    private String menuItem;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private String elderName;
}
