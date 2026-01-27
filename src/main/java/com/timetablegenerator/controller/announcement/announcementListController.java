package com.timetablegenerator.controller.announcement;

import com.timetablegenerator.model.announcementModel;
import com.timetablegenerator.model.role;
import com.timetablegenerator.repository.announcementRepo;
import com.timetablegenerator.service.announcementService;
import com.timetablegenerator.util.authSession; // Ensure this is imported
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
        String loginRole = authSession.getUser().getRole().toString();

        if (role.teacher.toString().equals(loginRole) && addNewBtn != null) {
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
        VBox card = new VBox(10);
        card.getStyleClass().add("announcement-card");
        card.setPadding(new Insets(15, 20, 15, 20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label(dateFormat.format(item.getCreated_date()));
        dateLabel.getStyleClass().add("card-date");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(dateLabel, spacer);

        if (!isTeacher) {
            MenuButton optionsBtn = new MenuButton("Option");
            optionsBtn.getStyleClass().add("card-options-button");
            optionsBtn.getItems().addAll(new MenuItem("Edit"), new MenuItem("Delete"));
            header.getChildren().add(optionsBtn);
        }

        HBox bodyContainer = new HBox(15);
        bodyContainer.setAlignment(Pos.TOP_LEFT);

        String imageName = switch (item.getType()) {
            case Timetable -> "timetableAnnouncement.png";
            case News -> "newsAnnouncement.png";
            default -> "othersAnnouncements.png";
        };

        ImageView clipart = new ImageView();
        var imageStream = getClass().getResourceAsStream("/assets/image/" + imageName);
        if (imageStream != null) {
            clipart.setImage(new Image(imageStream));
        }

        clipart.setFitWidth(70);
        clipart.setFitHeight(70);
        clipart.setPreserveRatio(true);

        VBox textContent = new VBox(2);
        HBox.setHgrow(textContent, Priority.ALWAYS);

        Label titleLabel = new Label(item.getTitle());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("card-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);

        String fullMessage = item.getMessage();
        int wordLimit = 20;
        String[] words = fullMessage.split("\\s+");

        if (words.length > wordLimit) {
            String truncated = String.join(" ", java.util.Arrays.copyOfRange(words, 0, wordLimit)) + "...";
            messageLabel.setText(truncated);
            Hyperlink readMore = new Hyperlink("Read more");
            readMore.getStyleClass().add("card-read-more");
            readMore.setOnAction(e -> {
                boolean isExpanded = readMore.getText().equals("Read more");
                messageLabel.setText(isExpanded ? fullMessage : truncated);
                readMore.setText(isExpanded ? "Show less" : "Read more");
            });
            textContent.getChildren().addAll(titleLabel, messageLabel, readMore);
        } else {
            messageLabel.setText(fullMessage);
            textContent.getChildren().addAll(titleLabel, messageLabel);
        }

        bodyContainer.getChildren().addAll(clipart, textContent);
        card.getChildren().addAll(header, bodyContainer);

        return card;
    }

    @FXML
    private void openCreateForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/announcement/announcementCreate.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create New Announcement");

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
}
