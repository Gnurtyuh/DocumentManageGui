package com.project.gui.controller;

import com.project.gui.model.DocumentDto;
import com.project.gui.service.AesServiceGui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class ReceiveDataController {

    @FXML public Button btnReceive;
    @FXML private Button btnFileBrowse;
    @FXML private Button btnDownload;
    @FXML private Button btnEye;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Label lblFilePath;

    private File encryptedFile;     // File mã hóa được chọn
    private boolean passwordVisible = false;
    private Long documentId;
    private String fileName;


    @FXML
    private void initialize() {
        System.out.println("✅ ReceiveDataController loaded!");

        // Mặc định ẩn trường hiển thị mật khẩu
        passwordVisibleField.setVisible(false);
        passwordVisibleField.setManaged(false);

        // Đồng bộ dữ liệu giữa hai ô
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
    public void setEncryptedFile(Long documentId ,String file) {
        this.documentId = documentId;
        this.fileName = file;
        if (fileName != null) {
            lblFilePath.setText("📦 File được nhận: " + fileName);
            System.out.println("📥 File nhận từ trang trước: " + fileName);
            System.out.println("📥 File nhận từ trang trước: " + documentId);
        }
    }
    // === 1. Chọn tập tin mã hóa (.enc) ===
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tập tin cần giải mã");

        Stage stage = (Stage) btnFileBrowse.getScene().getWindow();
        encryptedFile = fileChooser.showOpenDialog(stage);
        fileName = encryptedFile.getName();
        lblFilePath.setText("📂 Đã chọn: " + encryptedFile.getName());
        System.out.println("📂 File được chọn: " + encryptedFile.getAbsolutePath());
    }

    // === 2. Khi bấm “Download” → vừa giải mã vừa tải về luôn ===
    @FXML
    private void handleDownload() {

        String password = passwordField.getText();
        if (password == null || password.isEmpty()) {
            showAlert("⚠️ Vui lòng nhập mật khẩu để giải mã!", Alert.AlertType.WARNING);
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn nơi lưu file giải mã");
            fileChooser.setInitialFileName(fileName); // gợi ý tên file
            Stage stage = (Stage) btnDownload.getScene().getWindow();
            File saveFile = fileChooser.showSaveDialog(stage);
            String decryptedFilePath = AesServiceGui.decrypt(password,fileName,saveFile.getAbsolutePath());

            showAlert("✅ File đã giải mã thành công!\n📁 File trên server: " + decryptedFilePath,
                    Alert.AlertType.INFORMATION);
            System.out.println("💾 File đã giải mã trên server: " + decryptedFilePath);


            showAlert("✅ Đã tải file về: " + saveFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
            System.out.println("📂 File lưu tại: " + saveFile.getAbsolutePath());



        } catch (Exception e) {
            showAlert("❌ Giải mã thất bại!\nSai mật khẩu hoặc file bị lỗi.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // === 3. Giả lập “Nhận dữ liệu” ===
    @FXML
    private void handleReceiveData() throws IOException {

        // 1️⃣ Tạo FXMLLoader để tải giao diện mới
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml"));
        Parent root = loader.load();

        // 2️⃣ Lấy controller của trang ReceiveData
        PrimaryController primaryController = loader.getController();

        // 3️⃣ Truyền file sang
        primaryController.handleLog(documentId);

        // 4️⃣ Đổi scene
        Stage stage = (Stage) btnReceive.getScene().getWindow();

        stage.setHeight(939);
        stage.setScene(new Scene(root));
        stage.setTitle("Màn hình nhận dữ liệu");
        stage.show();
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
