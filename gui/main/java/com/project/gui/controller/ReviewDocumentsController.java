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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewDocumentsController {

    public AnchorPane contentArea;
    public TextField searchField;
    public Button btnSearch;
    public Button btnRefresh;
    @FXML
    private VBox documentContainer;

    private List<DocumentDto> allDocuments;

    @FXML
    public void initialize() {
        UserDto currentUser = UserServiceGui.getUserByUsername(SessionManager.getUsername());
        allDocuments = fetchDocumentsByRole(currentUser);

        if (allDocuments == null || allDocuments.isEmpty()) {
            Label emptyLabel = new Label("Không có tài liệu nào để xem.");
            emptyLabel.getStyleClass().add("empty-label");
            documentContainer.getChildren().add(emptyLabel);
            return;
        }

        showDocuments(allDocuments);

        btnSearch.setOnAction(e -> searchDocuments());
        btnRefresh.setOnAction(e -> showDocuments(allDocuments));
    }

    private List<DocumentDto> fetchDocumentsByRole(UserDto user) {
        return switch (user.getRoleLevel()) {
            case 3 -> DocumentServiceGui.getDocumentByUser(SessionManager.getUsername());
            case 2 -> DocumentServiceGui.getDocumentByDepartmentId(user.getDepartmentDto().getDepartmentId());
            case 1 -> DocumentServiceGui.getDocumentByDepartmentName(user.getDepartmentDto().getDepartmentName());
            default -> List.of();
        };
    }

    private void showDocuments(List<DocumentDto> documents) {
        documentContainer.getChildren().clear();
        documents.forEach(doc -> documentContainer.getChildren().add(createDocumentBox(doc)));
    }

    private void searchDocuments() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            showDocuments(allDocuments);
            return;
        }

        List<DocumentDto> filtered = allDocuments.stream()
                .filter(d ->
                        (d.getTitle() != null && d.getTitle().toLowerCase().contains(keyword)) ||
                                (d.getDescription() != null && d.getDescription().toLowerCase().contains(keyword)) ||
                                (d.getFilePath() != null && d.getFilePath().toLowerCase().contains(keyword)) ||
                                (d.getUserDto() != null && d.getUserDto().getUsername().toLowerCase().contains(keyword)) ||
                                (d.getDepartmentDto() != null && d.getDepartmentDto().getDepartmentName().toLowerCase().contains(keyword))
                )
                .collect(Collectors.toList());

        showDocuments(filtered);
    }

    private VBox createDocumentBox(DocumentDto document) {
        VBox box = new VBox(10);
        box.getStyleClass().add("document-box");
        box.setPrefWidth(850);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);

        addRow(grid, 0, "Tên tài liệu:", document.getTitle());
        addRow(grid, 1, "Người gửi:", document.getUserDto().getFullName());
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

    private void addRow(GridPane grid, int rowIndex, String label, String value) {
        grid.add(new Label(label), 0, rowIndex);
        grid.add(new Label(value != null ? value : "—"), 1, rowIndex);
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "Không xác định";
        OffsetDateTime odt = timestamp.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(timestamp.toInstant()));
        return odt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private void openDocument(DocumentDto document) {
        if (document.getDocumentId() == null || document.getFilePath() == null) {
            showAlert("⚠️!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml"));
            Parent root = loader.load();

            // Gửi dữ liệu sang controller khác
            PrimaryController primaryController = loader.getController();
            primaryController.handleReceive(document.getDocumentId(), document.getFilePath());

            // Chuyển sang giao diện mới (Stage)
            Stage stage = (Stage) documentContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1253, 939));
            stage.setTitle("Document Manager");
            stage.show();

        } catch (IOException e) {
            showAlert("❌ Lỗi khi mở tài liệu: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
