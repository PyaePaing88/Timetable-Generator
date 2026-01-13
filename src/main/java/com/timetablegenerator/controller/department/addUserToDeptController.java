package com.timetablegenerator.controller.department;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.service.userService;
import com.timetablegenerator.repository.userRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class addUserToDeptController {

    @FXML private TableView<userModel> allUsersTable;
    @FXML private TableColumn<userModel, String> colAllUserName;

    @FXML private TableView<userModel> deptUsersTable;
    @FXML private TableColumn<userModel, String> colDeptUserName;

    private final userService service;
    private int currentDeptId;

    private final ObservableList<userModel> availableUsers = FXCollections.observableArrayList();
    private final ObservableList<userModel> assignedUsers = FXCollections.observableArrayList();

    public addUserToDeptController() {
        this.service = new userService(new userRepo());
    }

    @FXML
    public void initialize() {
        colAllUserName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colDeptUserName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        allUsersTable.setItems(availableUsers);
        deptUsersTable.setItems(assignedUsers);
    }

    public void loadDepartmentUser(int deptId) {
        this.currentDeptId = deptId;
        try {
            List<userModel> available = service.getUsersForDept();

            List<userModel> assigned = service.getUserByDepartmentId(deptId);

            availableUsers.setAll(available);
            assignedUsers.setAll(assigned);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void moveToDept() {
        userModel selected = allUsersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            availableUsers.remove(selected);
            assignedUsers.add(selected);
        }
    }

    @FXML
    private void moveFromDept() {
        userModel selected = deptUsersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            assignedUsers.remove(selected);
            availableUsers.add(selected);
        }
    }

    @FXML
    private void handleSave() {
        try {
            for (userModel user : assignedUsers) {
                user.setDepartment_id(currentDeptId);
                service.saveUser(user);
            }

            for (userModel user : availableUsers) {
                user.setDepartment_id(0);
                service.saveUser(user);
            }

            showAlert("Success", "Department users updated successfully!");
            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Failed to save: " + e.getMessage());
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        Stage stage = (Stage) allUsersTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}