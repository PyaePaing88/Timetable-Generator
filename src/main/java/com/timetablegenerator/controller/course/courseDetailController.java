package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.service.courseService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class courseDetailController {

    @FXML
    private TextField txtId, txtName, txtDepartment, txtSubjectCode, txtAcademicLevel;

    private final courseService service = new courseService(new courseRepo());

    public void loadCourseData(int id) {
        try {
            courseModel course = service.getCourseById(id);
            if (course != null) {
                txtId.setText(String.valueOf(course.getId()));
                txtName.setText(course.getCourse_name());
                txtSubjectCode.setText(course.getSubject_code());

                txtDepartment.setText(course.getDepartment_name());
                txtAcademicLevel.setText(course.getAcademicLevel());

                setFieldsReadOnly();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFieldsReadOnly() {
        txtId.setEditable(false);
        txtName.setEditable(false);
        txtDepartment.setEditable(false);
        txtSubjectCode.setEditable(false);
        txtAcademicLevel.setEditable(false);
    }

    @FXML
    private void closeWindow() {
        if (txtId.getScene() != null) {
            Stage stage = (Stage) txtId.getScene().getWindow();
            stage.close();
        }
    }
}