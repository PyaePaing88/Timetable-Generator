package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.service.classCourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class linkClassAndCourseController {
    @FXML
    private TableView<classModel> allClassTable;
    @FXML
    private TableColumn<classModel, String> colAllClassName;

    @FXML
    private TableView<classModel> courseClassTable;
    @FXML
    private TableColumn<classModel, String> colCourseClassName;

    private final classCourseService service = new classCourseService();
    private int currentCourseId;

    private final ObservableList<classModel> availableClasses = FXCollections.observableArrayList();
    private final ObservableList<classModel> assignedClasses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colAllClassName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass_name()));
        colCourseClassName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass_name()));

        allClassTable.setItems(availableClasses);
        courseClassTable.setItems(assignedClasses);
    }

    public void loadData(int courseId) {
        this.currentCourseId = courseId;
        try {
            availableClasses.setAll(service.getUnlinkedClasses(courseId));
            assignedClasses.setAll(service.getLinkedClasses(courseId));
        } catch (Exception e) {
            showAlert("Error", "Failed to load classes: " + e.getMessage());
        }
    }

    @FXML
    private void moveToCourse() {
        classModel selected = allClassTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            availableClasses.remove(selected);
            assignedClasses.add(selected);
        }
    }

    @FXML
    private void moveFromCourse() {
        classModel selected = courseClassTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            assignedClasses.remove(selected);
            availableClasses.add(selected);
        }
    }

    @FXML
    private void handleSave() {
        try {
            service.updateClassCourseLinks(currentCourseId, assignedClasses);
            showAlert("Success", "Course classes updated successfully!");
            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) allClassTable.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}