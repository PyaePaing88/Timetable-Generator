package com.timetablegenerator.controller;

import com.timetablegenerator.mainApp;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;

public class teacherLayoutController {
    @FXML
    private StackPane contentArea;
    @FXML
    private HBox topNav;
    @FXML
    private Label nameLabel;

    @FXML
    public void initialize() {
        userModel user = authSession.getUser();
        nameLabel.setText(user != null ? "Hello: " + user.getName() : "No user logged in");

        loadView("/view/timetable/timetableListViewOnly.fxml", null);

        autoSelectButton("Timetable");
    }

    private void autoSelectButton(String buttonText) {
        topNav.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(btn -> buttonText.equalsIgnoreCase(btn.getText().trim()))
                .findFirst()
                .ifPresent(this::applyActiveStyle);
    }

    private void loadView(String fxml, ActionEvent event) {
        try {
            URL url = getClass().getResource(fxml);
            if (url == null) {
                System.err.println("FXML not found: " + fxml);
                return;
            }

            Node view = FXMLLoader.load(url);
            contentArea.getChildren().setAll(view);

            if (event != null && event.getSource() instanceof Button) {
                applyActiveStyle((Button) event.getSource());
            }
        } catch (Exception e) {
            System.err.println("Error loading: " + fxml);
            e.printStackTrace();
        }
    }

    private void applyActiveStyle(Button targetButton) {
        topNav.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().remove("active");
                updateButtonIcon(btn, false);
            }
        });

        targetButton.getStyleClass().add("active");
        updateButtonIcon(targetButton, true);
    }

    private void updateButtonIcon(Button button, boolean isActive) {
        if (button.getGraphic() instanceof ImageView) {
            ImageView iv = (ImageView) button.getGraphic();
            if (iv.getImage() != null) {
                String currentUrl = iv.getImage().getUrl();
                String newUrl;

                if (isActive) {
                    newUrl = currentUrl.replace("/normal/", "/active/");
                } else {
                    newUrl = currentUrl.replace("/active/", "/normal/");
                }

                if (!currentUrl.equals(newUrl)) {
                    try {
                        iv.setImage(new Image(newUrl));
                    } catch (Exception e) {
                        System.err.println("Icon not found: " + newUrl);
                    }
                }
            }
        }
    }

    @FXML
    private void showAnnouncement(ActionEvent event) {
        loadView("/view/announcement/announcementList.fxml", event);
    }

    @FXML
    private void showTimetable(ActionEvent event) {
        loadView("/view/timetable/timetableListViewOnly.fxml", event);
    }

    @FXML
    private void showAvailabilityList(ActionEvent event) {
        loadView("/view/availability/availabilityListTeacher.fxml", event);
    }

    @FXML
    private void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Logout");
        alert.setHeaderText(null);

        alert.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    authSession.clear();
                    mainApp.getInstance().showLogin();
                });
    }
}