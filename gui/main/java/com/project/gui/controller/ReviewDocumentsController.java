package com.project.gui.controller;

import com.project.gui.model.DepartmentDto;
import com.project.gui.model.DocumentDto;
import com.project.gui.model.SessionManager;
import com.project.gui.model.UserDto;
import com.project.gui.service.DepartmentServiceGui;
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
        List<DocumentDto> documentDtoList = List.of();
        UserDto userDto = UserServiceGui.getUserByUsername(SessionManager.getUsername());
        if (userDto.getRoleLevel() == 3){
            documentDtoList = DocumentServiceGui.getDocumentByUser(SessionManager.getUsername());
        }
        if (userDto.getRoleLevel() == 2){
            documentDtoList = DocumentServiceGui.getDocumentByDepartmentId(userDto.getDepartmentDto().getDepartmentId());
        }
        if  (userDto.getRoleLevel() == 1){
            documentDtoList = DocumentServiceGui.getDocumentByDepartmentName(userDto.getDepartmentDto().getDepartmentName());
        }
        for(DocumentDto documentDto:documentDtoList){
            String departmentName = documentDto.getDepartmentDto().getDepartmentName();
            String division= documentDto.getDepartmentDto().getDivision();
            String date = convertTime(documentDto.getUploadDate());
            documentContainer.getChildren().add(createDocumentBox(documentDto.getDocumentId(),documentDto.getTitle(),documentDto.getUserDto().getUsername(),
                    date,documentDto.getDescription(),
                    documentDto.getFilePath(),departmentName+"-"+division));
        }
    }

    private VBox createDocumentBox(Long documentId,String title, String username, String date, String description, String filePath, String departmentName) {
        VBox box = new VBox(10);
        box.getStyleClass().add("document-box");
        box.setPrefWidth(850);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);

        grid.add(new Label("Tên tài liệu:"), 0, 0);
        grid.add(new Label(title), 1, 0);

        grid.add(new Label("Người gửi:"), 0, 1);
        grid.add(new Label(username), 1, 1);

        grid.add(new Label("Ngày gửi:"), 0, 2);
        grid.add(new Label(date), 1, 2);

        grid.add(new Label("Trạng thái:"), 0, 3);
        grid.add(new Label(description), 1, 3);

        grid.add(new Label("File tài liệu: "), 0, 4);
        grid.add(new Label(filePath), 1, 4);

        grid.add(new Label("Phòng ban:"), 0, 5);
        grid.add(new Label(departmentName), 1, 5);

        Button viewBtn = new Button("Xem");
        viewBtn.getStyleClass().add("view-button");
        viewBtn.setOnAction(e -> {
            try {
                handleGoToReceivePage(documentId,filePath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox btnBox = new HBox(viewBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        box.getChildren().addAll(grid, btnBox);
        return box;
    }

    public String convertTime(Timestamp date) {
        OffsetDateTime odt = date.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(date.toInstant()));
        return odt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    private void handleGoToReceivePage(Long documentId ,String fileName) throws IOException {
        if (documentId == null || fileName == null) {
            showAlert();
            return;
        }

        // 1️⃣ Tạo FXMLLoader để tải giao diện mới
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml"));
        Parent root = loader.load();

        // 2️⃣ Lấy controller của trang ReceiveData
        PrimaryController primaryController = loader.getController();

        // 3️⃣ Truyền file sang
        primaryController.handleReceive(documentId, fileName);

        // 4️⃣ Đổi scene
        Stage stage = (Stage) documentContainer.getScene().getWindow();
        stage.setWidth(1253);
        stage.setHeight(939);
        stage.setScene(new Scene(root));
        stage.setTitle("Màn hình nhận dữ liệu");
        stage.show();
    }
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("⚠️ Bạn cần chọn file trước!");
        alert.showAndWait();
    }
}
