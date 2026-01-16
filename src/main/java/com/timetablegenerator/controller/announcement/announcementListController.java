package com.timetablegenerator.controller.announcement;

import com.timetablegenerator.model.announcementModel;
import com.timetablegenerator.repository.announcementRepo;
import com.timetablegenerator.service.announcementService;
import com.timetablegenerator.util.authSession; // Ensure this is imported
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class announcementListController {

    @FXML
    private VBox cardContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button addNewBtn;

    private final announcementService service;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    private boolean isTeacher;

    public announcementListController() {
        this.service = new announcementService(new announcementRepo());
    }

    @FXML
    public void initialize() {
        String role = authSession.getUser().getRole().toString();
        this.isTeacher = "teacher".equalsIgnoreCase(role);

        if (isTeacher && addNewBtn != null) {
            addNewBtn.setVisible(false);
            addNewBtn.setManaged(false);
        }

        scrollPane.setFitToWidth(true);
        loadAnnouncements("");
    }

    @FXML
    private void handleSearch() {
        loadAnnouncements(searchField.getText().trim());
    }

    private void loadAnnouncements(String search) {
        try {
            cardContainer.getChildren().clear();
            List<announcementModel> list = service.getAnnouncements(1, 50, search);

            for (announcementModel item : list) {
                cardContainer.getChildren().add(createAnnouncementCard(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createAnnouncementCard(announcementModel item) {
        VBox card = new VBox(8);
        card.getStyleClass().add("announcement-card");
        card.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label adminLabel = new Label("ADMIN");
        adminLabel.getStyleClass().add("card-admin-tag");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(adminLabel, spacer);

        if (!isTeacher) {
            MenuButton optionsBtn = new MenuButton("Option");
            optionsBtn.getStyleClass().add("card-options-button");

            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("Delete");

//            editItem.setOnAction(e -> handleEdit(item));
//            deleteItem.setOnAction(e -> handleDelete(item));
            optionsBtn.getItems().addAll(editItem, deleteItem);

            header.getChildren().add(optionsBtn);
        }

        Label titleLabel = new Label(item.getTitle());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        Label messageLabel = new Label(item.getMessage());
        messageLabel.getStyleClass().add("card-message");
        messageLabel.setWrapText(true);
        messageLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label dateLabel = new Label("Posted " + dateFormat.format(item.getCreated_date()));
        dateLabel.getStyleClass().add("card-date");

        card.getChildren().addAll(header, titleLabel, messageLabel, dateLabel);
        return card;
    }
}
