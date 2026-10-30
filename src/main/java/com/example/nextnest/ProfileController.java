package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileController {
    Stage stage;
    Scene s3, s4, s5, s6, s7, s8;


    private User loggedInUser;

    @FXML
    private FlowPane userPostFlowPane;

    @FXML
    private final int loggedInUserId = Integer.parseInt(LogSignUtils.loggedInUserId);


    //fxml label for name, location and  phone number

    @FXML
    private Label name;
    @FXML
    private Label address;
    @FXML
    private Label phn_no;

    //profile pic
    @FXML
    private ImageView pro_pic;
    @FXML
    private Button pro_pic_btn;
    @FXML
    private Button save_pic_btn;

    private final String Profile_path = null;

    private byte[] profileImageBytes;


    // Replace 'OtherController' with your actual controller class name

    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container

    private void applyTheme(Pane parent, String color) {
        // Apply theme to the parent and its children
        parent.setStyle("-fx-background-color: " + color + ";");

        for (var child : parent.getChildren()) {
            if (child instanceof AnchorPane) {
                child.setStyle("-fx-background-color: " + color + ";");
            } else if (child instanceof Rectangle) {
                ((Rectangle) child).setFill(javafx.scene.paint.Paint.valueOf(color));
            } else if (child instanceof Pane) {
                applyTheme((Pane) child, color);
            }
        }
    }


    @FXML
    public void initialize() {
        loadUserProfile();
        loadUserPosts();
        loadUserDetails();
        // Observe theme changes and apply them to both root panes
        ThemeManager.getInstance().currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            applyTheme(rootPane1, newValue);
            applyTheme(rootPane2, newValue);
        });

        // Set the initial theme
        applyTheme(rootPane1, ThemeManager.getInstance().getCurrentTheme());
        applyTheme(rootPane2, ThemeManager.getInstance().getCurrentTheme());
    }


    @FXML
    public void loadUserPosts() {
        userPostFlowPane.getChildren().clear();
        userPostFlowPane.setStyle("-fx-background-color: #36454F; -fx-padding: 20; -fx-hgap: 15; -fx-vgap: 15;");

        if (loggedInUserId == -1) {
            showErrorAlert("Invalid User", "No logged-in user found!");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            System.out.println("Database connection established.");
            String query = "SELECT post_id, title, template_path FROM Posts WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUserId);

            ResultSet rs = stmt.executeQuery();
            System.out.println("Query executed successfully.");

            boolean hasPosts = false;
            while (rs.next()) {
                int postId = rs.getInt("post_Id");
                String title = rs.getString("title");
                String templatePath = rs.getString("template_path");

                System.out.println("Post found: ID=" + postId + ", Title=" + title);

                hasPosts = true;
                VBox postBox = new VBox(10);
                postBox.setPrefWidth(280);  // Decreased width
                postBox.setStyle("""
                            -fx-padding: 15;
                            -fx-border-color: #dcdcdc;
                            -fx-border-width: 1;
                            -fx-border-radius: 10;
                            -fx-background-color: #ffffff;
                            -fx-background-radius: 10;
                            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);
                        """);

                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                // Query to get reaction counts from the 'Reactions' table
                String reactionsQuery = """
                            SELECT 
                                SUM(CASE WHEN reaction_type = 'like' THEN 1 ELSE 0 END) AS likeCount,
                                SUM(CASE WHEN reaction_type = 'dislike' THEN 1 ELSE 0 END) AS dislikeCount
                            FROM Reactions
                            WHERE post_id = ?
                        """;
                PreparedStatement reactionStmt = conn.prepareStatement(reactionsQuery);
                reactionStmt.setInt(1, postId);
                ResultSet reactionRs = reactionStmt.executeQuery();
                int likeCount = 0, dislikeCount = 0;
                if (reactionRs.next()) {
                    likeCount = reactionRs.getInt("likeCount");
                    dislikeCount = reactionRs.getInt("dislikeCount");
                }

                // Add the template image if available
                ImageView templateImageView = null;
                if (templatePath != null && !templatePath.isEmpty()) {
                    System.out.println("Loading template image from: " + templatePath);
                    try {
                        Image templateImage = new Image(templatePath);
                        templateImageView = new ImageView(templateImage);
                        templateImageView.setFitWidth(260);
                        templateImageView.setPreserveRatio(true);
                        postBox.getChildren().add(templateImageView);
                    } catch (Exception e) {
                        System.out.println("Invalid template image path: " + templatePath);
                    }
                }

                // Create a HBox for reactions, "Details", and "More Options" (3-dot button)
                HBox reactionBox = new HBox(10);
                reactionBox.setStyle("-fx-alignment: center-left;");

                // Thumbs Up Button (with like count)
                Button thumbsUpButton = new Button("👍");
                thumbsUpButton.setStyle("-fx-font-size: 18px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 20; -fx-padding: 5 15;");
                thumbsUpButton.setOnAction(e -> reactToPost(postId, "like"));

                // Thumbs Down Button (with dislike count)
                Button thumbsDownButton = new Button("👎");
                thumbsDownButton.setStyle("-fx-font-size: 18px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 20; -fx-padding: 5 15;");
                thumbsDownButton.setOnAction(e -> reactToPost(postId, "dislike"));

                // Labels to show like and dislike counts
                Label likeCountLabel = new Label(likeCount + "");
                Label dislikeCountLabel = new Label(dislikeCount + "");

                likeCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                dislikeCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

                // Create the "More Options" Button (3 dots)
                MenuButton moreOptionsButton = new MenuButton("⋮");
                moreOptionsButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-text-fill: #2c3e50;");

                // Create the "Delete" MenuItem
                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
                deleteItem.setOnAction(event -> {
                    try (Connection deleteConn = DatabaseUtil.getConnection()) {
                        // Delete the post from the database
                        String deleteQuery = "DELETE FROM Posts WHERE post_id = ?";
                        PreparedStatement deleteStmt = deleteConn.prepareStatement(deleteQuery);
                        deleteStmt.setInt(1, postId);
                        int rowsAffected = deleteStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Post deleted successfully.");
                            // Remove the post from the UI
                            userPostFlowPane.getChildren().remove(postBox);
                        }
                    } catch (Exception e) {
                        System.out.println("Error deleting post:");
                        e.printStackTrace();
                        showErrorAlert("Deletion Error", "Failed to delete the post. Please try again.");
                    }
                });

                // Create the "Details" MenuItem
                MenuItem detailsItem = new MenuItem("Details");
                detailsItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;");
                detailsItem.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("postDetails.fxml"));
                        Scene postDetailsScene = new Scene(loader.load());
                        PostDetailsController controller = loader.getController();
                        controller.setPostId(postId);
                        Stage stage = (Stage) userPostFlowPane.getScene().getWindow();
                        stage.setScene(postDetailsScene);
                    } catch (Exception e) {
                        System.out.println("Error loading post details:");
                        e.printStackTrace();
                    }
                });

                // Add the "Delete" and "Details" options to the MenuButton
                moreOptionsButton.getItems().addAll(deleteItem, detailsItem);

                // Create the "Details" Button (now removed as it's inside the MenuButton)
                // Add reactions, counts, and "More Options" button to the reactionBox
                reactionBox.getChildren().addAll(thumbsUpButton, likeCountLabel, thumbsDownButton, dislikeCountLabel, moreOptionsButton);

                // Add all components to the VBox
                postBox.getChildren().addAll(titleLabel, reactionBox);

                // Add the postBox to the FlowPane
                userPostFlowPane.getChildren().add(postBox);
            }

            if (!hasPosts) {
                Label noPostsLabel = new Label("You haven't created any posts yet.");
                noPostsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                userPostFlowPane.getChildren().add(noPostsLabel);
            }
        } catch (Exception e) {
            System.out.println("Error fetching user posts:");
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to fetch user posts. Please try again.");
        }
    }


    // Method to handle reactions
    private void reactToPost(int postId, String reactionType) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check if the user has already reacted to this post
            String checkQuery = "SELECT * FROM Reactions WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, postId);
                checkStmt.setInt(2, Integer.parseInt(LogSignUtils.loggedInUserId)); // Assuming 'up' is the logged-in user
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    // User has already reacted, update their reaction
                    String updateQuery = "UPDATE Reactions SET reaction_type = ? WHERE post_id = ? AND user_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, reactionType);
                        updateStmt.setInt(2, postId);
                        updateStmt.setInt(3, Integer.parseInt(LogSignUtils.loggedInUserId));
                        updateStmt.executeUpdate();
                    }
                } else {
                    // User has not reacted yet, insert a new reaction
                    String insertQuery = "INSERT INTO Reactions (post_id, user_id, reaction_type) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, postId);
                        insertStmt.setInt(2, Integer.parseInt(LogSignUtils.loggedInUserId));
                        insertStmt.setString(3, reactionType);
                        insertStmt.executeUpdate();
                    }
                }

                // Optionally, reload posts or update the UI
                loadUserPosts();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadUserDetails() {
        if (loggedInUserId == -1) {
            showErrorAlert("Invalid User", "No logged-in user found!");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Query to get user information from the users table
            String query = """
                        SELECT name, mobile_number, address
                        FROM users
                        WHERE id = ?
                        LIMIT 1
                    """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUserId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("name");
                String mobileNumber = rs.getString("mobile_number");
                String userAddress = rs.getString("address");

                // Setting the labels
                name.setText(userName);
                phn_no.setText(mobileNumber);
                address.setText(userAddress);
            } else {
                // Show default message if no user details found
                name.setText("N/A");
                phn_no.setText("N/A");
                address.setText("N/A");
            }
        } catch (Exception e) {
            System.out.println("Error loading user details:");
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to fetch user details. Please try again.");
        }
    }


    /// profile pic
    // profile pic
    @FXML
    public void selectProfile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".jpg", ".png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Read the image as bytes
                profileImageBytes = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                // Display the image in the ImageView and ensure it fits the entire image (even if it's large)
                Image image = new Image(selectedFile.toURI().toString());
                pro_pic.setImage(image);
                pro_pic.setPreserveRatio(false); // Allow the image to stretch to fit the ImageView
                pro_pic.setFitWidth(pro_pic.getBoundsInLocal().getWidth()); // Set the width of the ImageView
                pro_pic.setFitHeight(pro_pic.getBoundsInLocal().getHeight()); // Set the height of the ImageView
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("File Error", "Unable to read the selected image file.");
            }
        }
    }


    // Method to save the profile picture to the database
    @FXML
    public void saveProfilePicture() {
        if (profileImageBytes == null) {
            showErrorAlert("No Image Selected", "Please select a profile picture before saving.");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String updateQuery = "UPDATE users SET profile_image_path = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setBytes(1, profileImageBytes); // Set the image bytes
            stmt.setInt(2, Integer.parseInt(LogSignUtils.loggedInUserId)); // User ID

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success!", "Profile picture updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                showErrorAlert("Update Failed", "Unable to update profile picture. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "An error occurred while updating the profile picture.");
            System.out.println("save " + loggedInUserId);
        }
    }

    // Method to load and display the user's profile picture
    @FXML
    public void loadUserProfile() {
        if (LogSignUtils.loggedInUserId == null) {
            showErrorAlert("Error!", "User is not logged in!");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT profile_image_path FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(LogSignUtils.loggedInUserId));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("profile_image_path");
                if (imageBytes != null) {
                    // Convert the bytes back to an image and display it
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    pro_pic.setImage(image);
                    pro_pic.setPreserveRatio(false); // Allow the image to stretch to fit the ImageView
                    pro_pic.setFitWidth(pro_pic.getBoundsInLocal().getWidth()); // Set the width of the ImageView
                    pro_pic.setFitHeight(pro_pic.getBoundsInLocal().getHeight()); // Set the height of the ImageView
                } else {
                    pro_pic.setImage(null); // Set to null if no profile picture is found
                }
            } else {
                showErrorAlert("Error", "No user profile found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to load user profile. Please try again.");
            System.out.println(loggedInUserId + " no propic ");
        }
    }


    @FXML
    public void logout(ActionEvent e) {
        // Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("Log out Window");
        alert.setContentText("Are you sure you want to log out?");

        // Set custom styles and icons for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("img.png"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f0f8ff; -fx-text-fill: #000000;");
        dialogPane.lookup(".header-panel").setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");
        dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-font-weight: bold; -fx-background-color: #6b1d1d; -fx-text-fill: #ffffff;");
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle("-fx-font-weight: bold; -fx-background-color: #FF6F61; -fx-text-fill: #ffffff;");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                // Load the login scene
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
                Scene loginScene = new Scene(fxmlLoader.load());

                // Determine the source of the event and get the stage
                Stage stage;
                if (e.getSource() instanceof MenuItem source) {
                    stage = (Stage) source.getParentPopup().getOwnerWindow();
                } else {
                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                }

                // Set the new scene
                stage.setScene(loginScene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();

                // Show error alert if scene loading fails
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to load the login scene.");
                errorAlert.showAndWait();
            }
        }
    }


    @FXML
    public void home(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        s3 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s3);
        stage.show();
    }

    @FXML
    public void yourpost(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Posts.fxml"));
        s4 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s4);
        stage.show();
    }

    @FXML
    public void settings(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Settings.fxml"));
            Scene s5 = new Scene(fxmlLoader.load());

            // Get the current Stage from the MenuItem
            MenuItem source = (MenuItem) event.getSource(); // Cast the source to MenuItem
            Stage stage = (Stage) source.getParentPopup().getOwnerWindow(); // Get the Stage

            // Set the new Scene
            stage.setScene(s5);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML:");
            e.printStackTrace();
        }
    }

    @FXML
    public void aboutUs(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AboutUs.fxml"));
        s6 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s6);
        stage.show();
    }

    @FXML
    public void Review(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Review&Complian.fxml"));
        s7 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s7);
        stage.show();
    }

    @FXML
    public void PostDeatils(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("postDetails.fxml"));
        s8 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s8);
        stage.show();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }


}
