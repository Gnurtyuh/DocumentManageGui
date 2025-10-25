package com.project.gui.controller;

import com.project.gui.model.DocumentDto;
import com.project.gui.model.SessionManager;
import com.project.gui.model.UserDto;
import com.project.gui.service.DocumentServiceGui;
import com.project.gui.service.UserServiceGui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewDocumentsController {

    @FXML
    private VBox documentContainer;

    @FXML
    public void initialize() {
        UserDto currentUser = UserServiceGui.getUserByUsername(SessionManager.getUsername());
        List<DocumentDto> documents = fetchDocumentsByRole(currentUser);

        // Nếu không có tài liệu nào
        if (documents == null || documents.isEmpty()) {
            Label emptyLabel = new Label("Không có tài liệu nào để xem.");
            emptyLabel.getStyleClass().add("empty-label");
            documentContainer.getChildren().add(emptyLabel);
            return;
        }

        // Hiển thị danh sách tài liệu
        documents.forEach(doc -> documentContainer.getChildren().add(createDocumentBox(doc)));
    }

    /** 🔹 Lấy danh sách tài liệu tùy theo vai trò người dùng */
    private List<DocumentDto> fetchDocumentsByRole(UserDto user) {
        return switch (user.getRoleLevel()) {
            case 3 -> DocumentServiceGui.getDocumentByUser(SessionManager.getUsername());
            case 2 -> DocumentServiceGui.getDocumentByDepartmentId(user.getDepartmentDto().getDepartmentId());
            case 1 -> DocumentServiceGui.getDocumentByDepartmentName(user.getDepartmentDto().getDepartmentName());
            default -> List.of();
        };
    }

    /** 🔹 Tạo một khung hiển thị thông tin tài liệu */
    private VBox createDocumentBox(DocumentDto document) {
        VBox box = new VBox(10);
        box.getStyleClass().add("document-box");
        box.setPrefWidth(850);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);

        addRow(grid, 0, "Tên tài liệu:", document.getTitle());
        addRow(grid, 1, "Người gửi:", document.getUserDto().getUsername());
        addRow(grid, 2, "Ngày gửi:", formatTimestamp(document.getUploadDate()));
        addRow(grid, 3, "Trạng thái:", document.getDescription());
        addRow(grid, 4, "File tài liệu:", document.getFilePath());
        addRow(grid, 5, "Phòng ban:", document.getDepartmentDto().getDepartmentName() + " - " + document.getDepartmentDto().getDivision());

        Button viewBtn = new Button("Xem");
        viewBtn.getStyleClass().add("view-button");
        viewBtn.setOnAction(e -> openDocument(document));

        HBox btnBox = new HBox(viewBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        box.getChildren().addAll(grid, btnBox);
        return box;
    }

    /** 🔹 Hàm phụ trợ để thêm một dòng vào GridPane */
    private void addRow(GridPane grid, int rowIndex, String label, String value) {
        grid.add(new Label(label), 0, rowIndex);
        grid.add(new Label(value != null ? value : "—"), 1, rowIndex);
    }

    /** 🔹 Chuyển đổi Timestamp sang định dạng dễ đọc */
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "Không xác định";
        OffsetDateTime odt = timestamp.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(timestamp.toInstant()));
        return odt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    /** 🔹 Mở giao diện xem tài liệu */
    private void openDocument(DocumentDto document) {
        if (document.getDocumentId() == null || document.getFilePath() == null) {
            showAlert("⚠️ Bạn cần chọn file hợp lệ trước!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml"));
            Parent root = loader.load();

            PrimaryController primaryController = loader.getController();
            primaryController.handleReceive(document.getDocumentId(), document.getFilePath());

            Stage stage = (Stage) documentContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1253, 939));
            stage.setTitle("Màn hình nhận dữ liệu");
            stage.show();

        } catch (IOException e) {
            showAlert("❌ Lỗi khi mở tài liệu: " + e.getMessage());
        }
    }

    /** 🔹 Hiển thị cảnh báo đơn giản */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
