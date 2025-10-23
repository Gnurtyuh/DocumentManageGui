package com.project.gui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepartmentDto {
    private Long departmentId;
    private String departmentName;
    private String division;
    private String description;
}
