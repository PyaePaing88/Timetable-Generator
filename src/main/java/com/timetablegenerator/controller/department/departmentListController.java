package com.timetablegenerator.controller.department;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.departmentService;
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

public class departmentListController {

    @FXML private TableView<departmentModel> departmentTable;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML private TableColumn<departmentModel, Integer> colId;
    @FXML private TableColumn<departmentModel, String> colName;
    @FXML private TableColumn<departmentModel, String> colMinor;
    @FXML private TableColumn<departmentModel, Void> colActions;

    private departmentService service;

    @FXML
    public void initialize() {
        departmentRepo repo = new departmentRepo();
        this.service = new departmentService(repo);

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartment_name()));

        colMinor.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isIs_minor() ? "Minor" : "Major"));


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
            List<departmentModel> dept = service.getDepartments(pageIndex + 1, rowsPerPage, searchText);

            departmentTable.getItems().setAll(dept);

        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalDepartmentCount(searchText);

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
            private final MenuItem addUser = new MenuItem("Add User");
            private final MenuItem deleteItem = new MenuItem("Delete");

            {
                actionMenu.getItems().addAll(viewItem, editItem, addUser, deleteItem);

                viewItem.setOnAction(event -> {
                    departmentModel dept = getTableView().getItems().get(getIndex());
                    handleView(dept.getId());
                });

                editItem.setOnAction(event -> {
                    departmentModel dept = getTableView().getItems().get(getIndex());
                    handleEdit(dept.getId());
                });

                addUser.setOnAction(event -> {
                    departmentModel dept = getTableView().getItems().get(getIndex());
                    handleAddUserToDepartment(dept.getId());
                });

                deleteItem.setOnAction(event -> {
                    departmentModel dept = getTableView().getItems().get(getIndex());
                    handleDelete(dept.getId());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/department/departmentCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Department");

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/department/departmentDetail.fxml"));
            Parent root = loader.load();

            departmentDetailController controller = loader.getController();

            controller.loadDepartmentData(id);

            Stage stage = new Stage();
            stage.setTitle("department Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading User Detail View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/department/departmentEdit.fxml"));
            Parent root = loader.load();

            departmentEditController controller = loader.getController();
            controller.loadDepartmentData(id);

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

    private void handleAddUserToDepartment(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/department/addUserToDepartment.fxml"));
            Parent root = loader.load();

            addUserToDeptController controller = loader.getController();
            controller.loadDepartmentUser(id);

            Stage stage = new Stage();
            stage.setTitle("Add User To Department");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Add User to Department View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this department?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteDepartment(id);
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
