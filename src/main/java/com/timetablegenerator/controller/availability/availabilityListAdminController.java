package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.service.availabilityService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class availabilityListAdminController {

    @FXML
    private TableView<availabilityModel> availabilityTable;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML
    private TableColumn<availabilityModel, Integer> colId;
    @FXML
    private TableColumn<availabilityModel, String> colTeacher;
    @FXML
    private TableColumn<availabilityModel, String> colStatus;
    @FXML
    private TableColumn<availabilityModel, String> colRemark;
    @FXML
    private TableColumn<availabilityModel, String> colDay;
    @FXML
    private TableColumn<availabilityModel, String> colPeriod;
    @FXML
    private TableColumn<availabilityModel, Void> colActions;

    private availabilityService service;

    @FXML
    public void initialize() {
        availabilityRepo repo = new availabilityRepo();
        this.service = new availabilityService(repo);

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        colTeacher.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeacher_name()));

        colStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        colRemark.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRemark()));

        colDay.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDay_of_week().toString()));

        colPeriod.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPeriod().toString()));

        setupActionColumn();

        rowsPerPageCombo.setItems(FXCollections.observableArrayList(10, 20, 50, 100));
        rowsPerPageCombo.setValue(10);
        rowsPerPageCombo.setOnAction(e -> {
            this.rowsPerPage = rowsPerPageCombo.getValue();
            handleSearch();
        });

        pagination.setPageFactory(pageIndex -> {
            loadTableData(pageIndex);
            return new Label();
        });

        handleSearch();
    }

    private void loadTableData(int pageIndex) {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            List<availabilityModel> list = service.getAvailabilities(pageIndex + 1, rowsPerPage, searchText);
            availabilityTable.getItems().setAll(list);
        } catch (Exception e) {
            System.err.println("Error loading availability data: " + e.getMessage());
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalAvailabilityCount(searchText);

            int pageCount = (int) Math.ceil((double) totalRecords / rowsPerPage);
            if (pageCount <= 0) pageCount = 1;

            pagination.setPageCount(pageCount);
            pagination.setVisible(pageCount > 1);
            pagination.setManaged(pageCount > 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        updatePaginationCount();
        pagination.setCurrentPageIndex(0);
        loadTableData(0);
    }

    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final MenuButton actionMenu = new MenuButton("Options");
            private final MenuItem viewItem = new MenuItem("View Details");

            {
                actionMenu.getItems().addAll(viewItem);

                viewItem.setOnAction(event -> {
                    availabilityModel model = getTableView().getItems().get(getIndex());
                    handleView(model.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(actionMenu);
            }
        });
    }

    private void handleView(int id) {
        showForm("/view/availability/availabilityDetail.fxml", "Availability Details", id);
    }


    private void showForm(String fxmlPath, String title, int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof availabilityDetailController && id > 0) {
                ((availabilityDetailController) controller).loadAvailabilityData(id);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(searchField.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            handleSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this record?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteAvailability(id);
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}