package com.project.gui.controller;

import com.project.gui.model.DocumentDto;
import com.project.gui.model.LogDto;
import com.project.gui.model.SessionManager;
import com.project.gui.service.DocumentServiceGui;
import com.project.gui.service.LogServiceGui;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML private Label sentLabel;
    @FXML private Label receivedLabel;
    @FXML private Label clockLabel;
    @FXML private Button notifyButton;
    @FXML private VBox notificationBox;

    private boolean isVisible = false;

    @FXML
    public void initialize(){
        int countDocument = 0;
        int countLog = 0;
        List<DocumentDto> documentDtos = DocumentServiceGui.getDocumentByUser(SessionManager.getUsername());
        for (DocumentDto documentDto : documentDtos) {
            countDocument++;
        }
        List<LogDto> logDtos = LogServiceGui.getLogByUser(SessionManager.getUsername());
        for (LogDto logDto : logDtos) {
            countLog++;
        }
        sentLabel.setText(String.valueOf(countDocument));
        receivedLabel.setText(String.valueOf(countLog));
        startClock();
        notifyButton.setOnAction(e -> toggleNotification());
    }

    private void toggleNotification() {
        isVisible = !isVisible;
        if (isVisible) {
            notificationBox.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        } else {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationBox);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> notificationBox.setVisible(false));
            fadeOut.play();
        }
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalTime now = LocalTime.now();
            clockLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }
    
}
