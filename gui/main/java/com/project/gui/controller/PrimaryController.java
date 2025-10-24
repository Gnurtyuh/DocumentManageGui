package com.project.gui.controller;

import com.project.gui.model.DocumentDto;
import com.project.gui.model.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import static java.util.Objects.requireNonNull;


public class PrimaryController {

    @FXML
    public AnchorPane contentArea; // Vùng hiển thị nội dung động

    @FXML
    public void initialize() {
        if (contentArea.getScene() != null) {
            contentArea.getScene().setUserData(new FXMLLoader(getClass().getResource("/com/project/gui/home.fxml")));
        }
    }
    @FXML
    private void handleSendData() throws IOException {
        setContent("/com/project/gui/SendData.fxml");
    }

    public void handleReceive(Long documentId ,String fileName) throws IOException {
        if (documentId == null || fileName == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/ReceiveData.fxml"));
        Parent fxml = loader.load(); // load FXML

        contentArea.getChildren().setAll(fxml);

        ReceiveDataController receiveController = loader.getController();


        receiveController.setEncryptedFile(documentId, fileName);
    }

    public void handleLog(Long documentId) throws IOException {
        if (documentId == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/ApproveDocuments.fxml"));
        Parent fxml = loader.load(); // load FXML

        contentArea.getChildren().setAll(fxml);

        ApproveDocumentsController approveDocumentsController = loader.getController();

        approveDocumentsController.setDocumentId(documentId);
    }

    @FXML
    private void handleReceiveData() throws IOException {
        setContent("/com/project/gui/ReceiveData.fxml");
    }

    @FXML
    private void handleLogout() throws IOException {
        // Tải lại trang đăng nhập
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/gui/Login.fxml"));
        AnchorPane loginPane = loader.load();
        SessionManager.clean();
        // Lấy stage hiện tại
        Stage stage = (Stage) contentArea.getScene().getWindow();

        // Gán lại root sang Login.fxml
        Scene scene = new Scene(loginPane);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    private void openDashboard(ActionEvent event) throws IOException {
        Parent dashboard = FXMLLoader.load(requireNonNull(getClass().getResource("/com/project/gui/dashboard.fxml")));
        contentArea.getChildren().setAll(dashboard);
    }
    @FXML
    private void handleManageDocuments() {
        System.out.println("📚 Quản lý tài liệu được nhấn!");
        // TODO: Code để load giao diện quản lý tài liệu
    }

    @FXML
    private void handleApproveDocuments(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/com/project/gui/ApproveDocuments.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }
    @FXML
    private void handleReviewDocuments(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/com/project/gui/ReviewDocuments.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }
    public void setContent(String fxmlFile) throws IOException {
        Node node = FXMLLoader.load(getClass().getResource(fxmlFile));
        contentArea.getChildren().setAll(node);
    }

}
