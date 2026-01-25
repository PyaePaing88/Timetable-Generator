package com.timetablegenerator.controller.academicLevel;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.repository.academicLevelRepo;
import com.timetablegenerator.service.academicLevelService;
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

public class academicLevelListController {

    @FXML
    private TableView<academicLevelModel> academicLevelTable;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML
    private TableColumn<academicLevelModel, Integer> colId;
    @FXML
    private TableColumn<academicLevelModel, String> colName;
    @FXML
    private TableColumn<academicLevelModel, Void> colActions;

    private academicLevelService service;

    @FXML
    public void initialize() {
        academicLevelRepo repo = new academicLevelRepo();
        this.service = new academicLevelService(repo);

        colId.setCellValueFactory(cellData -> {
            int currentPage = pagination.getCurrentPageIndex();

            int rowIdx = academicLevelTable.getItems().indexOf(cellData.getValue());

            int continuousIndex = (currentPage * rowsPerPage) + rowIdx + 1;

            return new javafx.beans.property.SimpleObjectProperty<>(continuousIndex);
        });

        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getYear()));


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
            List<academicLevelModel> al = service.getAcademicLevel(pageIndex + 1, rowsPerPage, searchText);

            academicLevelTable.getItems().setAll(al);

        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalAcademicLevelCount(searchText);

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
                    academicLevelModel al = getTableView().getItems().get(getIndex());
                    handleView(al.getId());
                });

                editItem.setOnAction(event -> {
                    academicLevelModel al = getTableView().getItems().get(getIndex());
                    handleEdit(al.getId());
                });

                deleteItem.setOnAction(event -> {
                    academicLevelModel al = getTableView().getItems().get(getIndex());
                    handleDelete(al.getId());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/academicLevel/academicLevelCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Academic Level");

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/academicLevel/academicLevelDetail.fxml"));
            Parent root = loader.load();

            academicLevelDetailController controller = loader.getController();

            controller.loadAcademicLevel(id);

            Stage stage = new Stage();
            stage.setTitle("Academic Level Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Academic Level Detail View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/academicLevel/academicLevelEdit.fxml"));
            Parent root = loader.load();

            academicLevelEditController controller = loader.getController();
            controller.loadAcademicLevel(id);

            Stage stage = new Stage();
            stage.setTitle("Edit Department");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this Academic Level?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteAcademicLevel(id);
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
