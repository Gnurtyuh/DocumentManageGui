package com.project.gui.config;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class StageManager {
    private final Stage primaryStage;
    private final FXMLLoader loader;
    private final String applicationTitle;

    public StageManager( FXMLLoader loader,Stage primaryStage, String applicationTitle) {
        this.primaryStage = primaryStage;
        this.loader = loader;
        this.applicationTitle = applicationTitle;
    }
}
