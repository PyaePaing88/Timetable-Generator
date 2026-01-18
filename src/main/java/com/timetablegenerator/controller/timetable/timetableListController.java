package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.TimetableCardDTO;
import com.timetablegenerator.repository.timetableRepo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class timetableListController {

    @FXML
    private TextField searchField;
    @FXML
    private FlowPane cardContainer;
    @FXML
    private Pagination pagination;
    @FXML
    private VBox noResultsPlaceholder;

    private final timetableRepo repo = new timetableRepo();
    private List<TimetableCardDTO> allTimetables = new ArrayList<>();
    private final int CARDS_PER_PAGE = 6;

    @FXML
    public void initialize() {
        loadData();
        setupPagination();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updatePaginationForFilter(newVal));
    }

    private void loadData() {
        try {
            allTimetables = repo.findAllWithNames();
        } catch (SQLException e) {
            showError("Database Error", "Could not load timetables.");
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) getFilteredList().size() / CARDS_PER_PAGE);
        pagination.setPageCount(Math.max(1, pageCount));
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        cardContainer.getChildren().clear();
        List<TimetableCardDTO> filtered = getFilteredList();

        int fromIndex = pageIndex * CARDS_PER_PAGE;
        int toIndex = Math.min(fromIndex + CARDS_PER_PAGE, filtered.size());

        if (fromIndex < filtered.size()) {
            for (TimetableCardDTO item : filtered.subList(fromIndex, toIndex)) {
                cardContainer.getChildren().add(createCard(item));
            }
        }
        return new VBox();
    }

    private Node createCard(TimetableCardDTO data) {
        VBox card = new VBox(10);
        card.getStyleClass().add("timetable-card");
        card.getChildren().addAll(
                new Label("Department: " + data.getDepartmentName()),
                new Label("Class: " + data.getClassName()),
                new Label("Date: " + data.getScheduleDate())
        );

        Button viewBtn = new Button("View Full Schedule");
        viewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewBtn.setOnAction(e -> openTimetableModal(data.getTimetableId()));
        card.getChildren().add(viewBtn);

        return card;
    }

    private List<TimetableCardDTO> getFilteredList() {
        String filter = searchField.getText().toLowerCase();
        return allTimetables.stream()
                .filter(t -> t.getDepartmentName().toLowerCase().contains(filter) ||
                        t.getClassName().toLowerCase().contains(filter))
                .collect(Collectors.toList());
    }

    private void updatePaginationForFilter(String filter) {
        List<TimetableCardDTO> filtered = getFilteredList();
        boolean isEmpty = filtered.isEmpty();

        cardContainer.setVisible(!isEmpty);
        noResultsPlaceholder.setVisible(isEmpty);
        pagination.setVisible(!isEmpty);

        int pageCount = (int) Math.ceil((double) filtered.size() / CARDS_PER_PAGE);
        pagination.setPageCount(Math.max(1, pageCount));
        pagination.setCurrentPageIndex(0);
    }

    private void openTimetableModal(Integer id) {
        // Implement navigation to your Detail Modal FXML here
        System.out.println("Opening Timetable ID: " + id);
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void openGenerateModal() {
        try {
            // Load the FXML for the generation window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timetable/timetableCreate.fxml"));
            Parent root = loader.load();

            // Create a new Stage (Window)
            Stage stage = new Stage();
            stage.setTitle("Generate New Timetable");

            // Make it a Modal (blocks interaction with the main window)
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(cardContainer.getScene().getWindow());

            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optional: Refresh the list after the modal is closed
            stage.showAndWait();
            loadData(); // Reload cards to show the newly generated timetable
            setupPagination();

        } catch (IOException e) {
            showError("Navigation Error", "Could not open the generation window: " + e.getMessage());
            e.printStackTrace();
        }
    }
}