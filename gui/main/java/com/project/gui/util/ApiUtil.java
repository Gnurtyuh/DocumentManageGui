package com.project.gui.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gui.model.AuthenticationDto;
import com.project.gui.model.Authentications;
import com.project.gui.model.SessionManager;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ApiUtil {
    private static final String url = "http://localhost:8080";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public static <T> T get(String path,Class<T> responseType){
        try{
            String token = SessionManager.getToken();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url+path))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer "+token)
                    .GET()
                    .build();
            return returnMapper(request,responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T post(String path,Object body,Class<T> responseType){
        try{
            String token = SessionManager.getToken();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url+path))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer "+token)
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            return returnMapper(request,responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T put(String path,Object body,Class<T> responseType){
        try{
            String token = SessionManager.getToken();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url+path))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer "+token)
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            return returnMapper(request,responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean del(String path){
        try{
            String token = SessionManager.getToken();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url+path))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer "+token)
                    .DELETE()
                    .build();
            HttpResponse<String> response= client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode()==200 || response.statusCode() == 204;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    static <T> T returnMapper(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response= client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (responseType == String.class) {
            return (T) response.body();
        }else if (statusCode>=200 && statusCode<300) {
            return mapper.readValue(responseBody,responseType);
        } else {
            System.err.println(statusCode);
            System.err.println(responseBody);
            return null;
        }

    }
    public static String encrypt(String password, String filePath) {
        File inputFile = new File(filePath);
        String token = SessionManager.getToken();
        if (!inputFile.exists()) {
            throw new RuntimeException("❌ File không tồn tại: " + filePath);
        }

        OkHttpClient client = new OkHttpClient();

        // multipart/form-data
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("password", password)
                .addFormDataPart(
                        "file",
                        inputFile.getName(),
                        RequestBody.create(inputFile, MediaType.parse("application/octet-stream"))
                )
                .build();

        Request request = new Request.Builder()
                .url(url + "/user/aes/encrypt")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "Bearer "+token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("❌ Lỗi mã hóa file: " + response.code());
            }

            // Server trả về JSON { "message": "...", "path": "..." }
            String responseBody = response.body().string();
            System.out.println("📦 Server response: " + responseBody);
            return responseBody;
        } catch (IOException e) {
            throw new RuntimeException("❌ Lỗi kết nối đến server: " + e.getMessage(), e);
        }
    }

    public static byte[] getBytes(String path, String formData) {
        try {
            String token = SessionManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + path))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new IOException("❌ Server trả lỗi: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("❌ Lỗi khi gửi request: " + e.getMessage(), e);
        }
    }

    public static AuthenticationDto apiLogin(Authentications authentications) {
        try {
            String json = mapper.writeValueAsString(authentications); // ✅ Chuyển object sang JSON
            System.out.println("➡️ JSON gửi đi: " + json);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/user/auth"))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), AuthenticationDto.class);
            } else {
                System.err.println("❌ Status: " + response.statusCode());
                System.err.println("❌ Body: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("❌ Không tồn tại user hoặc lỗi kết nối", e);
        }
    }

}
