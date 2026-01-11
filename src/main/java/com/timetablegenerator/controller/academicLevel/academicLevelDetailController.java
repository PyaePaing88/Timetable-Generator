package com.timetablegenerator.controller.academicLevel;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.academicLevelRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.academicLevelService;
import com.timetablegenerator.service.departmentService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class academicLevelDetailController {
    @FXML
    private TextField txtId, txtName;

    private academicLevelService service = new academicLevelService(new academicLevelRepo());

    public void loadAcademicLevel(int userId) {
        try {
            academicLevelModel dept = service.getAcademicLevelById(userId);
            if (dept != null) {
                txtId.setText(String.valueOf(dept.getId()));
                txtName.setText(dept.getYear());
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
