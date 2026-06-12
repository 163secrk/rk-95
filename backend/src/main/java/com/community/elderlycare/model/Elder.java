package com.community.elderlycare.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Elder {
    private Long id;
    @NotBlank(message = "姓名不能为空")
    private String name;
    private String gender;
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于等于1")
    @Max(value = 150, message = "年龄必须小于等于150")
    private Integer age;
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的11位手机号")
    private String phone;
    private String address;
    private String emergencyContact;
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "请输入正确的11位紧急联系电话")
    private String emergencyPhone;
    private LocalDateTime createdAt;
}
