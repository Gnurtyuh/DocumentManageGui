package com.project.gui.service;

import com.project.gui.model.AuthenticationDto;
import com.project.gui.model.Authentications;
import com.project.gui.util.ApiUtil;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;

import java.io.IOException;

public class AuthenticationServiceGui {
    public static AuthenticationDto login(Authentications authentications) throws IOException {
        return ApiUtil.apiLogin(authentications);
    }
}
