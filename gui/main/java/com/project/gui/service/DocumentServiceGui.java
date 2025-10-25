package com.project.gui.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gui.model.DocumentDto;
import com.project.gui.model.UserDto;
import com.project.gui.util.ApiUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class DocumentServiceGui {

    public static DocumentDto getDocumentById(Long id) {
        return ApiUtil.get("/user/document/"+id, DocumentDto.class);
    }
    public static DocumentDto createDocument(DocumentDto documentDto) {
        return ApiUtil.post("/user/document" ,documentDto,DocumentDto.class);
    }
    public static DocumentDto updateDocument(Long id,DocumentDto documentDto){
        return ApiUtil.put("/user/document/" +id,documentDto,DocumentDto.class);
    }
    public boolean deleteDocument(Long id){
        return ApiUtil.del("/users/document/" + id);
    }
    public static List<DocumentDto> getDocumentByDepartmentId(Long departmentId) {
        DocumentDto[] documentDto=ApiUtil.get("/user/document/departmentId?departmentId=" + departmentId, DocumentDto[].class);
        return Arrays.asList(documentDto);
    }
    public static List<DocumentDto> getDocumentByUser(String username){
        DocumentDto[] documentDto =ApiUtil.get("/user/document/username/" + username, DocumentDto[].class);
        return Arrays.asList(documentDto);
    }
    public static List<DocumentDto> getDocumentByDepartmentName(String departmentName) {

        departmentName = URLEncoder.encode(departmentName, StandardCharsets.UTF_8);
        DocumentDto[] documentDto =ApiUtil.get("/user/document?departmentName=" + departmentName, DocumentDto[].class);
        return Arrays.asList(documentDto);
    }
}
