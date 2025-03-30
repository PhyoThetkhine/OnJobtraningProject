package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private int userId;
    private String currentPassword;
    private String newPassword;
}
