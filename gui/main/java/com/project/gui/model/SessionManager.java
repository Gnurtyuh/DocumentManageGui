package com.project.gui.model;

public class SessionManager {
    private static String token;
    private static String username;
    public static void setToken(String jwt){
        token = jwt;
    }
    public static String getToken(){
        return token;
    }
    public static String getUsername(){
        return username;
    }
    public static void setUsername(String username){
        SessionManager.username = username;
    }
    public static void clean(){
        token = null;
        username = null;
    }
}
