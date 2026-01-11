package com.timetablegenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class mainApp extends Application {
    private static mainApp instance;
    private Stage primaryStage;

    public mainApp() {
        instance = this;
    }

    public static mainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Timetable Generator");
        showMainLayout();
        stage.show();
    }

    private void setScene(String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);

            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLandingPage() {
        setScene("/view/landingPage/landingPage.fxml");
    }

    public void showLogin() {
        setScene("/view/auth/login.fxml");
    }

    public void showMainLayout() {
        setScene("/view/mainLayout.fxml");
    }

    public void showDashboard() {
        setScene("/view/dashboard/dashboard.fxml");
    }

    public void showUserList() {
        setScene("/view/user/userList.fxml");
    }

    public void showDepartmentList() {
        setScene("/view/department/departmentList.fxml");
    }

    public void showClassList() {
        setScene("/view/class/classList.fxml");
    }

    public void showCourseList() {
        setScene("/view/course/courseList.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}