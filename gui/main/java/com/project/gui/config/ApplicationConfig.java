package com.project.gui.config;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

public class ApplicationConfig {
    private final FXMLLoader loader;
    private final String applicationTitle;
    public ApplicationConfig(FXMLLoader loader,
                             @Value("${application.title}") String applicationTitle) {
        this.loader = loader;
        this.applicationTitle = applicationTitle;

    }
    @Bean
    @Lazy
    public StageManager stageManager(Stage stage){
        return new StageManager(loader,stage,applicationTitle);
    }
}
