package com.project.gui.model;

import lombok.Data;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Short roleLevel;
    private DepartmentDto departmentDto;

}