package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.service.courseService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class courseDetailController {
    @FXML
    private TextField txtId, txtName;

    private courseService service = new courseService(new courseRepo());

    public void loadCourseData(int id) {
        try {
            courseModel course = service.getCourseById(id);
            if (course != null) {
                txtId.setText(String.valueOf(course.getId()));
                txtName.setText(course.getCourse_name());
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
