package com.project.gui.model;

import lombok.Data;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentDto {
    private long documentId;
    private String title;
    private String description;
    private Timestamp uploadDate;
    private String filePath;
    private UserDto userDto;
    private DepartmentDto departmentDto;

}
