package com.timetablegenerator.controller.user;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.userRepo;
import com.timetablegenerator.service.userService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class userDetailController {

    @FXML private TextField txtId, txtName, txtEmail, txtPhone, txtRole, txtStatus;
    @FXML private CheckBox chkChangePassword;

    private final userService service = new userService(new userRepo());

    public void loadUserData(int userId) {
        try {
            userModel user = service.getUserById(userId);
            if (user != null) {
                txtId.setText(String.valueOf(user.getId()));
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmail());
                txtPhone.setText(user.getPhone());
                txtRole.setText(user.getRole() != null ? user.getRole().name() : "N/A");
                txtStatus.setText(user.isIs_active() ? "Active" : "Inactive");
                chkChangePassword.setSelected(user.isChange_password());
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