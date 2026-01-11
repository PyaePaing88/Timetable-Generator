package com.timetablegenerator.controller.user;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.userRepo;
import com.timetablegenerator.service.userService;
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

public class userListController {

    @FXML private TableView<userModel> userTable;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML private TableColumn<userModel, Integer> colId;
    @FXML private TableColumn<userModel, String> colName;
    @FXML private TableColumn<userModel, String> colEmail;
    @FXML private TableColumn<userModel, String> colPhone;
    @FXML private TableColumn<userModel, String> colRole;
    @FXML private TableColumn<userModel, String> colActive;
    @FXML private TableColumn<userModel, Void> colActions;

    private userService service;

    @FXML
    public void initialize() {
        userRepo repo = new userRepo();
        this.service = new userService(repo);

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        colEmail.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        colPhone.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));

        colRole.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getRole() != null ? cellData.getValue().getRole().name() : ""));

        colActive.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isIs_active() ? "Active" : "Inactive"));

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
            List<userModel> users = service.getUsers(pageIndex + 1, rowsPerPage, searchText);

            userTable.getItems().setAll(users);

        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalUserCount(searchText);

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
                    userModel user = getTableView().getItems().get(getIndex());
                    handleView(user.getId());
                });

                editItem.setOnAction(event -> {
                    userModel user = getTableView().getItems().get(getIndex());
                    handleEdit(user.getId());
                });

                deleteItem.setOnAction(event -> {
                    userModel user = getTableView().getItems().get(getIndex());
                    handleDelete(user.getId());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user/userCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New User");

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user/userDetail.fxml"));
            Parent root = loader.load();

            userDetailController controller = loader.getController();

            controller.loadUserData(id);

            Stage stage = new Stage();
            stage.setTitle("User Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading User Detail View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user/userEdit.fxml"));
            Parent root = loader.load();

            userEditController controller = loader.getController();

            controller.loadUserData(id);

            Stage stage = new Stage();
            stage.setTitle("User Edit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading User Edit View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this user?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteUser(id); // Your soft delete logic
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
