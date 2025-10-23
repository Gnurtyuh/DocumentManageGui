package com.project.gui.service;

import com.project.gui.model.DocumentDto;
import com.project.gui.util.ApiUtil;

import javax.crypto.CipherInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class AesServiceGui {
    public static String encrypt(String password,String filePath) {
        return ApiUtil.encrypt(password, filePath);
    }
    public static String decrypt(String password, String filename, String outputPath) {
        try {
            String formData = "password=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
                    + "&filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8);

            byte[] decryptedData = ApiUtil.getBytes("/user/aes/decrypt", formData);

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(decryptedData);
                System.out.println("✅ Giải mã thành công: " + outputPath);
                return "✅ Giải mã thành công: " + outputPath;
            }


        } catch (Exception e) {
            System.err.println("❌ Lỗi giải mã: " + e.getMessage());
            return "Lỗi giải mã";
        }
    }

}
