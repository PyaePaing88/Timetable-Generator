package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.TimetableDetailDTO;
import com.timetablegenerator.service.timetableService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class timetableDetailController {
    @FXML
    private GridPane timetableGrid;
    @FXML
    private Label lblHeaderInfo;

    private final timetableService service = new timetableService();

    private final Map<String, Integer> dayToRow = Map.of(
            "Monday", 1, "Tuesday", 2, "Wednesday", 3, "Thursday", 4, "Friday", 5
    );

    public void loadTimetableData(Integer timetableId) throws SQLException {
        timetableGrid.getChildren().removeIf(node -> node instanceof VBox);

        List<TimetableDetailDTO> details = service.getTimetableListByHeader(timetableId);

        for (TimetableDetailDTO slot : details) {
            VBox cell = createCell(slot);

            int column = slot.getPeriod();
            Integer row = dayToRow.get(slot.getDay());

            if (row != null && column >= 1) {
                timetableGrid.add(cell, column, row);
            }
        }
    }

    private VBox createCell(TimetableDetailDTO data) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-color: #bdc3c7; -fx-padding: 5; -fx-background-color: #ffffff;");

        Label course = new Label(data.getSubjectCode());
        course.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label teacher = new Label(data.getTeacherName());
        teacher.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(course, teacher);
        return box;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) timetableGrid.getScene().getWindow();
        stage.close();
    }
}
