package com.project.gui.service;

import com.project.gui.model.DepartmentDto;
import com.project.gui.model.DocumentDto;
import com.project.gui.util.ApiUtil;

public class DepartmentServiceGui {
    public static DepartmentDto getDepartmentDto(Long departmentId) {
        return ApiUtil.get("/admin/department/"+departmentId , DepartmentDto.class);
    }
}
