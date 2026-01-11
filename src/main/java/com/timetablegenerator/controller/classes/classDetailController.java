package com.timetablegenerator.controller.classes;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.service.classService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class classDetailController {
    @FXML
    private TextField txtId, txtName, txtDepartment;

    private classService service = new classService(new classRepo());

    public void loadClassData(int id) {
        try {
            classModel classes = service.getClassById(id);
            if (classes != null) {
                txtId.setText(String.valueOf(classes.getId()));
                txtName.setText(classes.getClass_name());
                txtDepartment.setText(classes.getDepartment_name());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}
