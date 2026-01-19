package com.timetablegenerator.controller;

import com.timetablegenerator.mainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class landingPageController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadView("/view/timetable/timetableListViewOnly.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            contentArea.getChildren().clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentArea.getChildren().add(view);

        } catch (IOException e) {
            System.err.println("Error switching views: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogin() {
        mainApp.getInstance().showLogin();
    }

}
