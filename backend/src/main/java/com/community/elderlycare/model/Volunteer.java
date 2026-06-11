package com.community.elderlycare.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Volunteer {
    private Long id;
    private String name;
    private String phone;
    private String skill;
    private String status;
    private LocalDateTime createdAt;
}
