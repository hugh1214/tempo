package com.hyozlet.tempo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class TempoApplication extends Application {
    @Override
    public void start(Stage stage) {
        var root = new StackPane(new Label("Tempo"));
        var scene = new Scene(root, 640, 400);

        stage.setTitle("Tempo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
