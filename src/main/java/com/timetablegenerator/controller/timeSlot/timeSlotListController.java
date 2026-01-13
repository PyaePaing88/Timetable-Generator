package com.timetablegenerator.controller.timeSlot;

import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;
import com.timetablegenerator.service.timeSlotService;
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

public class timeSlotListController {

    @FXML private TableView<timeSlotModel> userTable;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML private TableColumn<timeSlotModel, Integer> colId;
    @FXML private TableColumn<timeSlotModel, String> colDay;
    @FXML private TableColumn<timeSlotModel, String> colPeriod;
    @FXML private TableColumn<timeSlotModel, String> colStartTime;
    @FXML private TableColumn<timeSlotModel, String> colEndTime;
    @FXML private TableColumn<timeSlotModel, Void> colActions;

    private timeSlotService service;

    @FXML
    public void initialize() {
        timeSlotRepo repo = new timeSlotRepo();
        this.service = new timeSlotService(repo);

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        colDay.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDay_of_week() != null ? cellData.getValue().getDay_of_week().name() : ""));

        colPeriod.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getPeriod())));

        colStartTime.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getStart_time())));

        colEndTime.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getEnd_time())));

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
            List<timeSlotModel> time = service.getTimeSlot(pageIndex + 1, rowsPerPage, searchText);

            userTable.getItems().setAll(time);

        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalTimeSlotCount(searchText);

            int pageCount = (int) Math.ceil((double) totalRecords / rowsPerPage);
            if (pageCount <= 0) pageCount = 1;

            pagination.setPageCount(pageCount);

            boolean hasMultiplePages = pageCount > 1;
            pagination.setVisible(hasMultiplePages);
            pagination.setManaged(hasMultiplePages);

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
            private final MenuItem viewItem = new MenuItem("View More");
            private final MenuItem editItem = new MenuItem("Edit");
            private final MenuItem deleteItem = new MenuItem("Delete");

            {
                actionMenu.getItems().addAll(viewItem, editItem, deleteItem);

                viewItem.setOnAction(event -> {
                    timeSlotModel time = getTableView().getItems().get(getIndex());
                    handleView(time.getId());
                });

                editItem.setOnAction(event -> {
                    timeSlotModel time = getTableView().getItems().get(getIndex());
                    handleEdit(time.getId());
                });

                deleteItem.setOnAction(event -> {
                    timeSlotModel time = getTableView().getItems().get(getIndex());
                    handleDelete(time.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionMenu);
                }
            }
        });
    }

    @FXML
    private void openCreateForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timeSlot/timeSlotCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Time Slot");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.initOwner(searchField.getScene().getWindow());

            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Create Form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleView(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timeSlot/timeSlotDetail.fxml"));
            Parent root = loader.load();

            timeSlotDetailController controller = loader.getController();

            controller.loadTimeSlotData(id);

            Stage stage = new Stage();
            stage.setTitle("Time Slot Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Time Slot Detail View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timeSlot/timeSlotEdit.fxml"));
            Parent root = loader.load();

            timeSlotEditController controller = loader.getController();

            controller.loadTimeSlotData(id);

            Stage stage = new Stage();
            stage.setTitle("Time Slot Edit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Time Slot Edit View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this Time Slot?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteTimeSlot(id);
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
