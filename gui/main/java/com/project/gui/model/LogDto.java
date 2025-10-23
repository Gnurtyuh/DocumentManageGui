package com.project.gui.model;

import lombok.Data;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogDto {
    private long logId;
    private String action;
    private String target;
    private String status;
    private String description;
    private DocumentDto documentDto;
    private Timestamp createdAt;
    private Timestamp completedAt;
    private DepartmentDto departmentDto;
    private UserDto userDto;


}
