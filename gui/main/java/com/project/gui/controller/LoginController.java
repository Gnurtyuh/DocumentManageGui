package com.project.gui.controller;

import com.project.gui.model.AuthenticationDto;
import com.project.gui.model.Authentications;
import com.project.gui.model.SessionManager;
import com.project.gui.service.AuthenticationServiceGui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtName;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField textField;
    @FXML
    private Button btnTogglePassword;
    @FXML
    private Label lblError;

    private boolean isVisible = false;

    @FXML
    private void togglePassword() {
        if (isVisible) {
            // Hiện chế độ ẩn mật khẩu
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
            btnTogglePassword.setText("👁");
        } else {
            // Hiện mật khẩu dạng text
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            textField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            btnTogglePassword.setText("🙈");
        }
        isVisible = !isVisible;
    }

    @FXML
    private void handleLogin() {
        String username = txtName.getText();
        String password = isVisible ? textField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // 🟢 Tạo request body
            Authentications auth = new Authentications(username, password);

            // 🟢 Gọi service đăng nhập
            AuthenticationDto response = AuthenticationServiceGui.login(auth);

            if (response != null && response.getToken() != null) {
                lblError.setText("Đăng nhập thành công!");
                SessionManager.setToken(response.getToken());
                SessionManager.setUsername(username);
                // 🟢 Chuyển sang trang home.fxml
                Stage stage = (Stage) txtName.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();

            } else {
                lblError.setText("Sai email hoặc mật khẩu!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Lỗi khi đăng nhập: " + e.getMessage());
        }
    }
}
