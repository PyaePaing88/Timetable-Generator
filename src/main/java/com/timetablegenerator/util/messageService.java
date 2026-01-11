package com.timetablegenerator.util;

import javafx.scene.control.Alert;

public class messageService {

    public void showInfo(String message) {
        show(Alert.AlertType.INFORMATION, "Information", message);
    }

    public void showError(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }

    public void showWarning(String message) {
        show(Alert.AlertType.WARNING, "Warning", message);
    }

    private void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
