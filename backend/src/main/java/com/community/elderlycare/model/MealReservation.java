package com.community.elderlycare.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MealReservation {
    private Long id;
    @NotNull(message = "请选择老人")
    private Long elderId;
    @NotBlank(message = "请选择用餐日期")
    private String mealDate;
    private String mealType;
    @NotBlank(message = "请输入菜品")
    private String menuItem;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private String elderName;
}
