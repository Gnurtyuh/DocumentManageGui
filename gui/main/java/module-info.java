module com.project.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires static lombok;
    requires java.sql;
    requires spring.beans;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires java.desktop;
    opens com.project.gui.model to com.fasterxml.jackson.databind;

    opens com.project.gui to javafx.fxml;
    exports com.project.gui;
    exports com.project.gui.controller;
    opens com.project.gui.controller to javafx.fxml;
}