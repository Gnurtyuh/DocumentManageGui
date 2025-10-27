package com.project.gui.controller;

import com.project.gui.model.*;
import com.project.gui.service.LogServiceGui;
import com.project.gui.service.UserServiceGui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ApproveDocumentsController {

    @FXML private VBox documentList;
    private final UserDto userDto = UserServiceGui.getUserByUsername(SessionManager.getUsername());
    private Long documentId;

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    @FXML
    public void initialize() {
        Platform.runLater(this::loadLogs);
    }

    private void loadLogs() {
        List<LogDto> logs = fetchLogsByRole();

        if (documentId != null) {
            logs = LogServiceGui.getLogByDocumentId(documentId);
        }

        documentList.getChildren().clear();
        logs.forEach(this::renderLogCard);
    }

    private List<LogDto> fetchLogsByRole() {
        int role = userDto.getRoleLevel();
        String username = SessionManager.getUsername();
        String deptName = userDto.getDepartmentDto().getDepartmentName();
        Long deptId = userDto.getDepartmentDto().getDepartmentId();

        return switch (role) {
            case 1 -> LogServiceGui.getLogByDepartmentName(deptName);
            case 2 -> LogServiceGui.getLogByDepartmentId(deptId);
            default -> LogServiceGui.getLogByUser(username);
        };
    }

    private void renderLogCard(LogDto logDto) {
        VBox card = new VBox(10);
        card.getStyleClass().add("document-card");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);

        DocumentDto doc = logDto.getDocumentDto();
        DepartmentDto dept = logDto.getDepartmentDto();
        UserDto user = logDto.getUserDto();

        String completed = logDto.getCompletedAt() != null ? convertTime(logDto.getCompletedAt()) : "—";

        addGridRow(grid, "Hành động:", logDto.getAction());
        addGridRow(grid, "Mục tiêu:", logDto.getTarget());
        addGridRow(grid, "Trạng thái:", logDto.getStatus());
        addGridRow(grid, "Mô tả:", logDto.getDescription());
        addGridRow(grid, "Tên tài liệu:", doc.getFilePath());
        addGridRow(grid, "Thời gian gửi:", convertTime(logDto.getCreatedAt()));
        addGridRow(grid, "Thời gian hoàn thành:", completed);
        addGridRow(grid, "Phòng ban:", dept.getDepartmentName() + " - " + dept.getDivision());
        addGridRow(grid, "Người thực hiện:", user.getUsername());

        // Nút chung
        Button viewBtn = new Button("Xem");
        viewBtn.getStyleClass().add("btn-view");
        viewBtn.setOnAction(e -> openDocument(doc.getDocumentId(), doc.getFilePath()));

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);


        if (logDto.getStatus().equals("CHỜ XÉT DUYỆT") && userDto.getRoleLevel() == 2) {
            logDto.setAction("TRƯỞNG PHÒNG PHÊ DUYỆT");
            buttons.getChildren().addAll(
                    createActionButton("Xác nhận", "ĐÃ ĐƯỢC PHÊ DUYỆT", logDto),
                    createActionButton("Từ chối", "TỪ CHỐI", logDto)
            );
        } else if (logDto.getStatus().equals("ĐÃ ĐƯỢC PHÊ DUYỆT") && userDto.getRoleLevel() == 1) {
            logDto.setAction("HOÀN THÀNH");
            buttons.getChildren().addAll(
                    createActionButton("Xác nhận", "XÉT DUYỆT THÀNH CÔNG", logDto),
                    createActionButton("Từ chối", "TỪ CHỐI", logDto)
            );
        }

        buttons.getChildren().add(viewBtn);
        card.getChildren().addAll(grid, buttons);
        documentList.getChildren().add(card);
    }

    private void addGridRow(GridPane grid, String label, String value) {
        int row = grid.getRowCount();
        grid.addRow(row, new Label(label), new Label(value != null ? value : "—"));
    }

    private Button createActionButton(String text, String status, LogDto log) {
        Button btn = new Button(text);
        btn.getStyleClass().add(text.equals("Xác nhận") ? "btn-approve" : "btn-reject");
        btn.setOnAction(e -> {
            LogServiceGui.updateLog(log.getLogId(), log, status);
            showAlert("Thao tác " + text.toLowerCase() + " thành công!");
            loadLogs(); // refresh lại UI
        });
        return btn;
    }

    private void openDocument(Long documentId, String fileName) {
        if (documentId == null || fileName == null) {
            showAlert("Thiếu thông tin tài liệu!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml"));
            Parent root = loader.load();
            PrimaryController primaryController = loader.getController();
            primaryController.handleReceive(documentId, fileName);

            Stage stage = (Stage) documentList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Màn hình nhận dữ liệu");
            stage.setHeight(939);
            stage.show();
        } catch (IOException e) {
            showAlert("Lỗi khi mở tài liệu: " + e.getMessage());
        }
    }

    private String convertTime(Timestamp ts) {
        OffsetDateTime odt = ts.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(ts.toInstant()));
        return odt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
