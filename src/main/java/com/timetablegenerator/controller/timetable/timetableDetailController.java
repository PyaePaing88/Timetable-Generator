package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.TimetableDetailDTO;
import com.timetablegenerator.service.timetableService;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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

    private Integer currentTimetableId;
    private String currentClassName;

    public void loadTimetableData(Integer timetableId, String class_name) throws SQLException {

        this.currentTimetableId = timetableId;
        this.currentClassName = class_name;

        String displayName = (class_name != null) ? class_name.trim() : "Unknown Class";
        lblHeaderInfo.setText("Weekly Timetable: " + displayName);

        timetableGrid.getChildren().removeIf(node -> node instanceof VBox);

        List<TimetableDetailDTO> details = service.getTimetableListByHeader(timetableId);

        for (TimetableDetailDTO slot : details) {
            if (slot.getTeacher_id() == null && slot.getCourseName() == null) {
                VBox cell = createLibraryCell(slot);

                int column = slot.getPeriod();
                Integer row = dayToRow.get(slot.getDay());

                if (row != null && column >= 1) {
                    timetableGrid.add(cell, column, row);
                }
            } else {
                VBox cell = createCell(slot);

                int column = slot.getPeriod();
                Integer row = dayToRow.get(slot.getDay());

                if (row != null && column >= 1) {
                    timetableGrid.add(cell, column, row);
                }
            }

        }
    }

    private VBox createCell(TimetableDetailDTO data) {
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("timetable-cell");
        box.setMinHeight(80);

        Label course = new Label(data.getCourseName().toUpperCase());
        course.getStyleClass().add("course-label");

        Label teacher = new Label(data.getTeacherName());
        teacher.getStyleClass().add("teacher-label");

        box.getChildren().addAll(course, teacher);

        if (data.isIs_leave()) {
            Label status = new Label("Leave");
            status.setStyle("-fx-font-size: 9px; -fx-text-fill: #3498db; -fx-font-weight: bold;");
            box.setStyle("-fx-background-color: #ffdfd6; -fx-border-color: #FAA18F;");
            box.getChildren().add(status);
        }

        var currentUser = authSession.getUser();
        if (currentUser != null && data.getTeacher_id() == currentUser.getId()) {
            box.getStyleClass().add("editable-cell");
            box.setCursor(javafx.scene.Cursor.HAND);

            box.setOnMouseClicked(event -> openEditPopup(data));
        }

        return box;
    }

    private VBox createLibraryCell(TimetableDetailDTO data) {
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("timetable-cell-library");
        box.setMinHeight(80);

        Label course = new Label("Library");
        course.getStyleClass().add("course-label");

        box.getChildren().addAll(course);

        return box;
    }

    private void openEditPopup(TimetableDetailDTO data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timetable/timetableEdit.fxml"));
            Parent root = loader.load();

            timetableEditController controller = loader.getController();
            controller.initData(data.getId());

            Stage stage = new Stage();
            stage.setTitle("Edit Timetable Status");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(timetableGrid.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();

            loadTimetableData(currentTimetableId, currentClassName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
