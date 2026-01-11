package com.timetablegenerator.controller;

import com.timetablegenerator.mainApp;
import javafx.fxml.FXML;

public class landingPageController {

    @FXML
    private void goLogin() {
        mainApp.getInstance().showLogin();
    }

}
