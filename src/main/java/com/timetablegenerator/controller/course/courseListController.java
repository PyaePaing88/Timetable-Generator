package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.service.courseService;
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

public class courseListController {

    @FXML
    private TableView<courseModel> courseTable;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;
    private int rowsPerPage = 10;

    @FXML
    private TableColumn<courseModel, Integer> colId;
    @FXML
    private TableColumn<courseModel, String> colName;
    @FXML
    private TableColumn<courseModel, String> colSubject_code;
    @FXML
    private TableColumn<courseModel, String> colDept;
    @FXML
    private TableColumn<courseModel, String> colLevel;
    @FXML
    private TableColumn<courseModel, Integer> colPeriodPerWeek;
    @FXML
    private TableColumn<courseModel, Void> colActions;

    private courseService service;

    @FXML
    public void initialize() {
        courseRepo repo = new courseRepo();
        this.service = new courseService(repo);

        colId.setCellValueFactory(cellData -> {
            int currentPage = pagination.getCurrentPageIndex();

            int rowIdx = courseTable.getItems().indexOf(cellData.getValue());

            int continuousIndex = (currentPage * rowsPerPage) + rowIdx + 1;

            return new javafx.beans.property.SimpleObjectProperty<>(continuousIndex);
        });

        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCourse_name()));

        colSubject_code.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubject_code()));

        colDept.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartment_name()));

        colLevel.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAcademicLevel()));

        colPeriodPerWeek.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPeriod_per_week()));

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
            List<courseModel> course = service.getCourses(pageIndex + 1, rowsPerPage, searchText);

            courseTable.getItems().setAll(course);

        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationCount() {
        try {
            String searchText = (searchField.getText() == null) ? "" : searchField.getText().trim();
            int totalRecords = service.getTotalCourseCount(searchText);

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
            private final MenuItem linkClass = new MenuItem("Link Class");
            private final MenuItem assignTeacher = new MenuItem("Assign Teacher");
            private final MenuItem deleteItem = new MenuItem("Delete");

            {
                actionMenu.getItems().addAll(viewItem, editItem, linkClass, assignTeacher, deleteItem);
                actionMenu.getStyleClass().add("table-options-button");

                viewItem.setOnAction(event -> {
                    courseModel course = getTableView().getItems().get(getIndex());
                    handleView(course.getId());
                });

                editItem.setOnAction(event -> {
                    courseModel course = getTableView().getItems().get(getIndex());
                    handleEdit(course.getId());
                });

                linkClass.setOnAction(event -> {
                    courseModel course = getTableView().getItems().get(getIndex());
                    handleLinkClassToCourse(course.getId());
                });

                assignTeacher.setOnAction(event -> {
                    courseModel course = getTableView().getItems().get(getIndex());
                    handleAssignUserToCourse(course.getId());
                });

                deleteItem.setOnAction(event -> {
                    courseModel course = getTableView().getItems().get(getIndex());
                    handleDelete(course.getId());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/course/courseCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Course");

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/course/courseDetail.fxml"));
            Parent root = loader.load();

            courseDetailController controller = loader.getController();

            controller.loadCourseData(id);

            Stage stage = new Stage();
            stage.setTitle("Course Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading Course Detail View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/course/courseEdit.fxml"));
            Parent root = loader.load();

            courseEditController controller = loader.getController();
            controller.loadCourseData(id);

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

    private void handleLinkClassToCourse(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/course/linkClassAndCourse.fxml"));
            Parent root = loader.load();

            linkClassAndCourseController controller = loader.getController();
            controller.loadData(id);

            Stage stage = new Stage();
            stage.setTitle("Link Class and Course");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAssignUserToCourse(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/course/assignUserToCourse.fxml"));
            Parent root = loader.load();

            assignUserWithCourseController controller = loader.getController();
            controller.loadData(id);

            Stage stage = new Stage();
            stage.setTitle("Assign Teacher to Course");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();
            handleSearch();

        } catch (IOException e) {
            System.err.println("Error loading View: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this Course?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteCourse(id);
                    handleSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
