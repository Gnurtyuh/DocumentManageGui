package com.project.gui.service;

import com.project.gui.model.LogDto;
import com.project.gui.util.ApiUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LogServiceGui {
    public static LogDto getLogById(long id) {
        return ApiUtil.get("user/log/"+id , LogDto.class);
    }
    public static LogDto updateLog(long logId,LogDto logDto,String Status)  {
        logDto.setStatus(Status);
        return ApiUtil.post("/user/log/"+logId,logDto,LogDto.class);
    }
    public static List<LogDto> getLogByUser (String username){
        LogDto[] logDto = ApiUtil.get("/user/log/userid/"+username, LogDto[].class);
        return Arrays.asList(logDto);
    }
    public static List<LogDto> getLogByDepartmentId(long departmentId) {
        LogDto[] logDto = ApiUtil.get("/user/log/department/"+departmentId, LogDto[].class);
        return Arrays.asList(logDto);
    }
    public static List<LogDto> getLogByDepartmentName(String departmentName) {
        LogDto[] logDto = ApiUtil.get("/user/log/departments/"+departmentName, LogDto[].class);
        return Arrays.asList(logDto);
    }
    public static List<LogDto> getLogByDocumentId(long documentId) {
        LogDto[] logDto = ApiUtil.get("/user/log/document/"+documentId, LogDto[].class);
        return Arrays.asList(logDto);
    }
    public static List<LogDto> getLogByMonth(long userId,int month) {
        LogDto[] logDto = ApiUtil.get("/user/log/userbymonth/"+ userId +"/search?month=" +month,LogDto[].class);
        return Arrays.asList(logDto);
    }
}
