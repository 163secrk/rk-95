package com.community.elderlycare.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Elder {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private LocalDateTime createdAt;
}
