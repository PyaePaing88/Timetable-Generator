package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.service.userCourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class assignUserWithCourseController {
    @FXML
    private TableView<userModel> allUserTable;
    @FXML
    private TableColumn<userModel, String> colAllUserName;

    @FXML
    private TableView<userModel> courseUserTable;
    @FXML
    private TableColumn<userModel, String> colCourseUserName;

    private final userCourseService service = new userCourseService();
    private int currentCourseId;

    private final ObservableList<userModel> availableUsers = FXCollections.observableArrayList();
    private final ObservableList<userModel> assignedUsers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colAllUserName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colCourseUserName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));

        allUserTable.setItems(availableUsers);
        courseUserTable.setItems(assignedUsers);
    }

    public void loadData(int courseId) {
        this.currentCourseId = courseId;
        try {
            availableUsers.setAll(service.getUnlinkedUsers(courseId));
            assignedUsers.setAll(service.getLinkedUsers(courseId));
        } catch (Exception e) {
            showAlert("Error", "Failed to load classes: " + e.getMessage());
        }
    }

    @FXML
    private void moveToCourse() {
        userModel selected = allUserTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            availableUsers.remove(selected);
            assignedUsers.add(selected);
        }
    }

    @FXML
    private void moveFromCourse() {
        userModel selected = courseUserTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            assignedUsers.remove(selected);
            availableUsers.add(selected);
        }
    }

    @FXML
    private void handleSave() {
        try {
            service.updateUserCourseLinks(currentCourseId, assignedUsers);
            showAlert("Success", "Instructors assigned successfully!");
            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Save failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        if (allUserTable.getScene() != null) {
            ((Stage) allUserTable.getScene().getWindow()).close();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
