package com.project.gui.controller;

import com.project.gui.model.DocumentDto;
import com.project.gui.model.SessionManager;
import com.project.gui.model.UserDto;
import com.project.gui.service.AesServiceGui;
import com.project.gui.service.DocumentServiceGui;
import com.project.gui.service.UserServiceGui;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;

public class SendDataController {
    @FXML public TextArea txtNote;
    @FXML public TextArea txtTitle;
    @FXML private Button btnBrowseFile;
    @FXML private Button btnDownload;
    @FXML private Button btnEye;
    @FXML private Button btnSend;
    @FXML private Label lblFilePath;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;

    private boolean passwordVisible = false;

    private File originalFile;   // file gốc người dùng chọn

    @FXML
    private void initialize() {
        System.out.println("✅ SendDataController loaded!");

        // Ẩn trường mật khẩu hiển thị ban đầu
        passwordVisibleField.setVisible(false);
        passwordVisibleField.setManaged(false);

        // Đồng bộ 2 field
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    // === Ẩn/hiện mật khẩu ===
    @FXML
    private void handleTogglePassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            btnEye.setText("🙈");
        } else {
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            btnEye.setText("👁");
        }
    }

    // === 1. Chọn tập tin để mã hóa ===
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tập tin cần mã hóa");
        Stage stage = (Stage) btnBrowseFile.getScene().getWindow();
        originalFile = fileChooser.showOpenDialog(stage);

        if (originalFile != null) {
            lblFilePath.setText("📂 Đã chọn: " + originalFile.getName());
            System.out.println("📂 File được chọn: " + originalFile.getAbsolutePath());
        }
    }

    // === 2. Khi bấm Download → Mã hóa và tải về luôn ===
    @FXML
    private void handleDownload() {
        if (originalFile == null) {
            showAlert("⚠️ Vui lòng chọn tập tin trước!", Alert.AlertType.WARNING);
            return;
        }

        String password = passwordField.getText();
        if (password == null || password.isEmpty()) {
            showAlert("⚠️ Vui lòng nhập mật khẩu để mã hóa!", Alert.AlertType.WARNING);
            return;
        }

        try {
            // --- Mã hóa bằng AES ---
            String filename = originalFile.getAbsolutePath();

            // --- Gọi API POST /user/encrypt ---
            String encryptedFilePath = AesServiceGui.encrypt(password, filename);
            showAlert("✅ File đã mã hóa thành công!\n📁 Đường dẫn file trên server: " + encryptedFilePath,
                    Alert.AlertType.INFORMATION);
            System.out.println("💾 File mã hóa lưu trên server: " + encryptedFilePath);

        } catch (Exception e) {
            showAlert("❌ Lỗi khi mã hóa/tải file: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // === 3. Gửi dữ liệu ===
    @FXML
    private void handleSendData() throws IOException {
        if (originalFile == null) {
            showAlert("⚠️ Chưa có file nào được chọn!", Alert.AlertType.WARNING);
            return;
        }
        DocumentDto documentDto = new DocumentDto();
        String username = SessionManager.getUsername();
        UserDto userDto = UserServiceGui.getUserByUsername(username);
        documentDto.setUserDto(userDto);
        documentDto.setDepartmentDto(userDto.getDepartmentDto());
        documentDto.setDescription(txtNote.getText());
        documentDto.setFilePath(originalFile.getName());
        documentDto.setTitle(txtTitle.getText());
        DocumentDto doc= DocumentServiceGui.createDocument(documentDto);
        showAlert("📤 Dữ liệu đã được gửi thành công!"+doc.getTitle(), Alert.AlertType.INFORMATION);
        System.out.println("📤 Gửi dữ liệu thành công!");
    }

    // === Hiển thị thông báo ===
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
