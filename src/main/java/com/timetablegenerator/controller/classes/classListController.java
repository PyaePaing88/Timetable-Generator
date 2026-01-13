package com.timetablegenerator.controller.classes;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.service.classService;
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

public class classListController {

    @FXML private TableView<classModel> classTable;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML private TableColumn<classModel, Integer> colId;
    @FXML private TableColumn<classModel, String> colName;
    @FXML private TableColumn<classModel, String> colDept;
    @FXML private TableColumn<classModel, Void> colActions;

    private classService service;

    @FXML
    public void initialize() {
        classRepo repo = new classRepo();
        this.service = new classService(repo);

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClass_name()));

        colDept.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartment_name()));

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
            List<classModel> classes = service.getClasses(pageIndex + 1, rowsPerPage, searchText);

            classTable.getItems().setAll(classes);

        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalClassCount(searchText);

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
                    classModel classes = getTableView().getItems().get(getIndex());
                    handleView(classes.getId());
                });

                editItem.setOnAction(event -> {
                    classModel classes = getTableView().getItems().get(getIndex());
                    handleEdit(classes.getId());
                });

                deleteItem.setOnAction(event -> {
                    classModel classes = getTableView().getItems().get(getIndex());
                    handleDelete(classes.getId());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/class/classCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Class");

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

    @FXML
    private void openGenerateForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/class/classGenerate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Class");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.initOwner(searchField.getScene().getWindow());

            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Generate Form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleView(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/class/classDetail.fxml"));
            Parent root = loader.load();

            classDetailController controller = loader.getController();

            controller.loadClassData(id);

            Stage stage = new Stage();
            stage.setTitle("Class Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Class Detail View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/class/classEdit.fxml"));
            Parent root = loader.load();

            classEditController controller = loader.getController();
            controller.loadClassData(id);

            Stage stage = new Stage();
            stage.setTitle("Edit Class");
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this Class?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteClass(id);
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
