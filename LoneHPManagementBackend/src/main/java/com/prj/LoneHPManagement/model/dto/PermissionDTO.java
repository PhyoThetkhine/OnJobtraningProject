package com.prj.LoneHPManagement.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDTO {
    private Integer id;
    private String name;

    public PermissionDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}