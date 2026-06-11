package com.community.elderlycare.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
    private Long volunteerId;
    private String name;
    private LocalDateTime createdAt;
}
