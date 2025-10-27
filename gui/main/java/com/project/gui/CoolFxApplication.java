package com.project.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class CoolFxApplication extends Application {
    private ConfigurableApplicationContext applicationContext;
    private static Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Login")); // 👈 CHỈ CẦN ĐỔI THÀNH "home"
        stage.setScene(scene);
        stage.setTitle("Document Manager");
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static javafx.scene.Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CoolFxApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(Main.class).run();
    }
    @Override
    public void stop(){
        applicationContext.close();
    }

}