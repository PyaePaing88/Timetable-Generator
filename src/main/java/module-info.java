module com.timetablegenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.timetablegenerator.model to javafx.base, javafx.fxml;
    opens com.timetablegenerator.controller to javafx.fxml;
    opens com.timetablegenerator to javafx.fxml;

    exports com.timetablegenerator;
    opens com.timetablegenerator.controller.user to javafx.fxml;
    opens com.timetablegenerator.controller.department to javafx.fxml;
    opens com.timetablegenerator.controller.classes to javafx.fxml;
    opens com.timetablegenerator.controller.course to javafx.fxml;
    opens com.timetablegenerator.controller.academicLevel to javafx.fxml;

}
