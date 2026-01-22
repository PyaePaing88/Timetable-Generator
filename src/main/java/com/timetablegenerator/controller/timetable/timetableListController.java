package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.TimetableCardDTO;
import com.timetablegenerator.service.timetableService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class timetableListController {

    @FXML
    private FlowPane cardContainer;
    @FXML
    private Button btnGenerateNew;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;
    @FXML
    private VBox noResultsPlaceholder;

    private int rowsPerPage = 10;
    private final timetableService service = new timetableService();

    @FXML
    public void initialize() {
        rowsPerPageCombo.setItems(FXCollections.observableArrayList(6, 12, 30, 60));
        rowsPerPageCombo.setValue(10);
        rowsPerPageCombo.setOnAction(e -> {
            this.rowsPerPage = rowsPerPageCombo.getValue();
            handleSearch();
        });

        pagination.setPageFactory(pageIndex -> {
            loadTimetableCards(pageIndex);
            return new Label();
        });

        handleSearch();
    }

    private void loadTimetableCards(int pageIndex) {
        try {
            cardContainer.getChildren().clear();
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();

            List<TimetableCardDTO> cards = service.getTimetablesPaginated(pageIndex + 1, rowsPerPage, searchText);

            if (cards.isEmpty()) {
                noResultsPlaceholder.setVisible(true);
                noResultsPlaceholder.setManaged(true);
            } else {
                noResultsPlaceholder.setVisible(false);
                noResultsPlaceholder.setManaged(false);
                for (TimetableCardDTO cardData : cards) {
                    cardContainer.getChildren().add(createCard(cardData));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalTimetableCount(searchText);

            int pageCount = (int) Math.ceil((double) totalRecords / rowsPerPage);
            if (pageCount <= 0) pageCount = 1;

            pagination.setPageCount(pageCount);

            boolean hasMultiplePages = pageCount > 1;
            pagination.setVisible(hasMultiplePages);
            pagination.setManaged(hasMultiplePages);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        updatePaginationCount();
        pagination.setCurrentPageIndex(0);
        loadTimetableCards(0);
    }

    private VBox createCard(TimetableCardDTO data) {
        VBox card = new VBox(10);
        card.getStyleClass().add("timetable-card");

        card.setPrefHeight(170);
        card.setAlignment(Pos.TOP_LEFT);

        card.prefWidthProperty().bind(
                cardContainer.widthProperty()
                        .subtract(cardContainer.getPadding().getLeft() + cardContainer.getPadding().getRight())
                        .subtract(cardContainer.getHgap() * 3)
                        .subtract(20)
                        .divide(4)
        );

        Label lblDept = new Label(data.getDepartmentName());
        lblDept.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
        lblDept.setWrapText(true);
        lblDept.setMinHeight(40);

        Label lblClass = new Label(data.getClassName());
        lblClass.setStyle("-fx-text-fill: #34495e;");

        Label lblYear = new Label("Year: " + data.getScheduleDate());
        lblYear.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnView = new Button("View timetable");
        btnView.getStyleClass().add("tg-btn");
        btnView.getStyleClass().add("tg-btn-primary");
        btnView.setMaxWidth(Double.MAX_VALUE);
        btnView.setOnAction(event -> openDetailsModal(data.getTimetableId(), data.getClassName()));

        card.getChildren().addAll(lblDept, lblClass, lblYear, spacer, btnView);
        return card;
    }

    @FXML
    private void handleGenerateNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timetable/timetableCreate.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Generate New Timetable");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnGenerateNew.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            handleSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDetailsModal(Integer timetableId, String class_name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timetable/timetableDetail.fxml"));
            Parent root = loader.load();
            timetableDetailController controller = loader.getController();
            controller.loadTimetableData(timetableId, class_name);

            Stage stage = new Stage();
            stage.setTitle("Timetable Detailed View");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}