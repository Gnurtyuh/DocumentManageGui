package com.project.gui.controller;

import com.project.gui.model.DocumentDto;
import com.project.gui.model.LogDto;
import com.project.gui.model.SessionManager;
import com.project.gui.model.UserDto;
import com.project.gui.service.LogServiceGui;
import com.project.gui.service.UserServiceGui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.concurrent.atomic.AtomicInteger;


public class ApproveDocumentsController {

    @FXML
    private VBox documentList;
    private final UserDto userDto = UserServiceGui.getUserByUsername(SessionManager.getUsername());
    private Long documentId;
    public void setDocumentId(Long documentId){
        this.documentId = documentId;
    }
    @FXML
    public void initialize()  {
        Platform.runLater(() -> {
            List<LogDto>logDtoList = List.of();
            logDtoList= LogServiceGui.getLogByUser(SessionManager.getUsername());
            if  (userDto.getRoleLevel() == 1){
                logDtoList = LogServiceGui.getLogByDepartmentName(userDto.getDepartmentDto().getDepartmentName());
            }
            else
            if (userDto.getRoleLevel() == 2){
                logDtoList = LogServiceGui.getLogByDepartmentId(userDto.getDepartmentDto().getDepartmentId());
            }else
            if (userDto.getRoleLevel() == 3){
                logDtoList = LogServiceGui.getLogByUser(SessionManager.getUsername());
            }
            if(documentId !=null){
                logDtoList = LogServiceGui.getLogByDocumentId(documentId);

            }
            for(LogDto logDto:logDtoList){
                Logs(logDto);
            }
        });
    }

    private void Logs(LogDto logDto) {
        String departmentName = logDto.getDepartmentDto().getDepartmentName();
        String division= logDto.getDepartmentDto().getDivision();
        String action = logDto.getAction();
        String target = logDto.getTarget();
        String status=logDto.getStatus();
        String description=logDto.getDescription();
        String document=logDto.getDocumentDto().getFilePath();
        String created=convertTime(logDto.getCreatedAt());
        String completed = null;
        if (logDto.getCompletedAt()!=null){
            completed=convertTime(logDto.getCompletedAt());
        }
        String dept=departmentName+"-"+division;
        String username=logDto.getUserDto().getUsername();
        VBox card = new VBox(10);
        card.getStyleClass().add("document-card");
        DocumentDto documentDto = logDto.getDocumentDto();
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);

        grid.add(new Label("Hành động:"), 0, 0);
        grid.add(new Label(action), 1, 0);

        grid.add(new Label("Mục tiêu:"), 0, 1);
        grid.add(new Label(target), 1, 1);

        grid.add(new Label("Trạng thái:"), 0, 2);
        grid.add(new Label(status), 1, 2);

        grid.add(new Label("Mô tả:"), 0, 3);
        grid.add(new Label(description), 1, 3);

        grid.add(new Label("Tên tài liệu:"), 0, 4);
        grid.add(new Label(document), 1, 4);

        grid.add(new Label("Thời gian gửi tài liệu:"), 0, 5);
        grid.add(new Label(created), 1, 5);

        grid.add(new Label("Thời gian hoàn thành:"), 0, 6);
        grid.add(new Label(completed), 1, 6);

        grid.add(new Label("Tên phòng ban:"), 0, 7);
        grid.add(new Label(dept), 1, 7);

        grid.add(new Label("Người thực hiện:"), 0, 8);
        grid.add(new Label(username), 1, 8);

        Button viewBtn = new Button("Xem");
        viewBtn.getStyleClass().add("btn-view");
        viewBtn.setOnAction(e -> {
            try {
                handleGoToReceivePage(documentDto.getDocumentId(), document);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        if (logDto.getStatus().equals("PENDING")&& userDto.getRoleLevel() == 2) {
            logDto.setAction("TRƯỞNG PHÒNG PHÊ DUYỆT");
            Button btnApprove = new Button("Xác nhận");
            btnApprove.getStyleClass().add("btn-approve");
            btnApprove.setOnAction(e -> { LogServiceGui.updateLog(logDto.getLogId(), logDto, "UPDATE");showAlert();});


            Button btnReject = new Button("Từ chối");
            btnReject.getStyleClass().add("btn-reject");
            btnReject.setOnAction(e -> {LogServiceGui.updateLog(logDto.getLogId(), logDto, "TỪ CHỐI");showAlert();});
            HBox buttons = new HBox(20, btnApprove, btnReject, viewBtn);
            buttons.setAlignment(Pos.CENTER);

            card.getChildren().addAll(grid, buttons);
        }else if(logDto.getStatus().equals("UPDATE")&& userDto.getRoleLevel() == 1) {
            logDto.setAction("HOÀN THÀNH");
            Button btnApprove = new Button("Xác nhận");
            btnApprove.getStyleClass().add("btn-approve");
            btnApprove.setOnAction(e ->{ LogServiceGui.updateLog(logDto.getLogId(), logDto, "XÉT DUYỆT THÀNH CÔNG");showAlert();});

            Button btnReject = new Button("Từ chối");
            btnReject.getStyleClass().add("btn-reject");
            btnReject.setOnAction(e -> {LogServiceGui.updateLog(logDto.getLogId(), logDto, "TỪ CHỐI");showAlert();});
            HBox buttons = new HBox(20, btnApprove, btnReject, viewBtn);
            buttons.setAlignment(Pos.CENTER);
            card.getChildren().addAll(grid, buttons);
        }else{
            HBox buttons = new HBox(20, viewBtn);
            buttons.setAlignment(Pos.CENTER);

            card.getChildren().addAll(grid, buttons);
        }


        documentList.getChildren().add(card);
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
        Stage stage = (Stage) documentList.getScene().getWindow();
        stage.setHeight(939);
        stage.setScene(new Scene(root));
        stage.setTitle("Màn hình nhận dữ liệu");
        stage.show();
    }
    private void showAlert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã chọn thành công!");
        alert.showAndWait();
    }
    private void showAlert1(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã chọn thành công!"+message);
        alert.showAndWait();
    }
}
