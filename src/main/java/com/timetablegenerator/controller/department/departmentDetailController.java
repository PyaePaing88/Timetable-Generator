package com.timetablegenerator.controller.department;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.departmentService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class departmentDetailController {
    @FXML
    private TextField txtId, txtName;

    private departmentService service = new departmentService(new departmentRepo());

    public void loadDepartmentData(int userId) {
        try {
            departmentModel dept = service.getDepartmentById(userId);
            if (dept != null) {
                txtId.setText(String.valueOf(dept.getId()));
                txtName.setText(dept.getDepartment_name());
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
