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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class HomeController {
    Scene scene;
    Scene s3;
    Scene s4;
    Scene s5;
    Scene s6;
    Scene s7;
    Scene s8;
    Stage stage;
    Image image1;
    LogSignUtils up;
    File file;
    Scene s2;

    //Post from database : Sifat
    @FXML
    private VBox buttonContainer; // Match the ID from Scene Builder
    @FXML
    private FlowPane postFlowpane;
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container


    //toggle group 1(tg1 in fxml)
    @FXML
    private RadioButton sale;
    @FXML
    private RadioButton rent;

    //toggle group 2(tg2)
    @FXML
    private RadioButton negotiable;
    @FXML
    private RadioButton non_negotiable;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;





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

        sale.setSelected(false);  // Unselect "Sale"
        rent.setSelected(false);  // Unselect "Rent"
        negotiable.setSelected(false);  // Unselect "Negotiable"
        non_negotiable.setSelected(false);
        loadPosts();

        // Listen for theme changes
        // Observe theme changes and apply them to both root panes
        ThemeManager.getInstance().currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            applyTheme(rootPane1, newValue);
            applyTheme(rootPane2, newValue);
        });

        // Set the initial theme
        applyTheme(rootPane1, ThemeManager.getInstance().getCurrentTheme());
        applyTheme(rootPane2, ThemeManager.getInstance().getCurrentTheme());

        sale.setOnAction(event -> loadPosts());  // When 'sale' radio button is selected, reload posts
        rent.setOnAction(event -> loadPosts());  // When 'rent' radio button is selected, reload posts
        negotiable.setOnAction(event -> loadPosts());  // When 'negotiable' radio button is selected, reload posts
        non_negotiable.setOnAction(event -> loadPosts());

        searchButton.setOnAction(event -> searchPosts());

    }

    public void loadPosts() {
        postFlowpane.getChildren().clear(); // Clear previous posts

        // Apply custom styles to the FlowPane
        postFlowpane.setStyle("-fx-background-color: #272926; -fx-padding: 20; -fx-hgap: 15; -fx-vgap: 15;");

        String saleOrRentCondition = "";
        String negotiableCondition = "";

        // Determine sorting/filtering criteria based on toggle groups
        if (sale.isSelected()) {
            saleOrRentCondition = "AND Posts.sale_or_rent = 'Sale'";
        } else if (rent.isSelected()) {
            saleOrRentCondition = "AND Posts.sale_or_rent = 'Rent'";
        }

        if (negotiable.isSelected()) {
            negotiableCondition = "AND Posts.negotiable = 'Negotiable'";
        } else if (non_negotiable.isSelected()) {
            negotiableCondition = "AND Posts.negotiable = 'Non-Negotiable'";
        }


        try (Connection conn = DatabaseUtil.getConnection()) {
            // Query to fetch the required fields with the sorting criteria
            String query = """
                SELECT 
                    Posts.post_id AS postId,
                    Users.name AS username, 
                    Posts.created_at, 
                    Posts.title, 
                    Posts.template_path
                FROM Posts
                JOIN Users ON Posts.user_id = Users.id
                WHERE 1 = 1
                """ + saleOrRentCondition + " " + negotiableCondition + """
                ORDER BY Posts.created_at DESC
                """;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int postId = rs.getInt("postId");
                String username = rs.getString("username");
                String createdAt = rs.getString("created_at");
                String title = rs.getString("title");
                String templatePath = rs.getString("template_path");

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

                // Create a VBox for the post with decreased width
                VBox postBox = new VBox(10);
                postBox.setStyle("""
                        -fx-padding: 15;
                        -fx-border-color: #dcdcdc;
                        -fx-border-width: 1;
                        -fx-border-radius: 10;
                        -fx-background-color: #ffffff;
                        -fx-background-radius: 10;
                        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);
                        -fx-pref-width: 280px; /* Decreased width */
                        -fx-pref-height: 400px;
                        """);

                // Add the username label
                Label usernameLabel = new Label("Posted by: " + username);
                usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

                // Add the creation time label
                Label createdAtLabel = new Label("Posted on: " + createdAt);
                createdAtLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                // Add the title label
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #34495e;");

                // Add the template image if available
                ImageView templateImageView = null;
                if (templatePath != null && !templatePath.isEmpty()) {
                    templateImageView = new ImageView(new Image(templatePath));
                    templateImageView.setFitWidth(260);
                    templateImageView.setFitHeight(200);
                    templateImageView.setPreserveRatio(false);
                    //templateImageView.setPreserveRatio(true);
                    templateImageView.setStyle("-fx-border-color: #dcdcdc; -fx-border-width: 1; -fx-border-radius: 5;");
                }

                // Create a HBox for reactions and "More Options" (3 dots)
                HBox reactionBox = new HBox(10);
                reactionBox.setStyle("-fx-alignment: center-left;"); // Align to the left

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


                // Create the "Details" MenuItem
                MenuItem detailsItem = new MenuItem("Details");
                detailsItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;");
                detailsItem.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("postDetails.fxml"));
                        Scene postDetailsScene = new Scene(loader.load());
                        PostDetailsController controller = loader.getController();
                        controller.setPostId(postId);
                        Stage stage = (Stage) postFlowpane.getScene().getWindow();
                        stage.setScene(postDetailsScene);
                    } catch (Exception e) {
                        System.out.println("Error loading post details:");
                        e.printStackTrace();
                    }
                });

                // Create the "Comment" MenuItem
                MenuItem commentItem = new MenuItem("Comment");
                commentItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");
                commentItem.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("postComment.fxml"));
                        Scene postCommentScene = new Scene(loader.load());
                        PostCommentController controller = loader.getController();
                        controller.setPostId(postId);  // Pass the postId to the PostCommentController
                        controller.loadComments(postId);  // Load comments for this post
                        Stage stage = (Stage) postFlowpane.getScene().getWindow();
                        stage.setScene(postCommentScene);
                    } catch (Exception e) {
                        System.out.println("Error loading comment page:");
                        e.printStackTrace();
                    }
                });

                // Add the "Delete", "Details", and "Comment" options to the MenuButton
                moreOptionsButton.getItems().addAll(detailsItem, commentItem);

                // Add reactions, counts, and "More Options" button to the reactionBox
                reactionBox.getChildren().addAll(thumbsUpButton, likeCountLabel, thumbsDownButton, dislikeCountLabel, moreOptionsButton);

                // Add all components to the VBox
                postBox.getChildren().addAll(usernameLabel, createdAtLabel, titleLabel);
                if (templateImageView != null) {
                    postBox.getChildren().add(templateImageView);
                }
                postBox.getChildren().add(reactionBox);

                // Add the postBox to the FlowPane
                postFlowpane.getChildren().add(postBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                loadPosts();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Profile(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Profile.fxml"));
        scene = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    //log out button to log in nterface with alert box
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

    //searching

    public void searchPosts() {
        String searchTerm = searchField.getText().trim();  // Get text from the search field

        if (searchTerm.isEmpty()) {
            loadPosts();  // If no search term, load all posts
        } else {
            searchUserPosts(searchTerm);
        }
    }

    private void searchUserPosts(String areaName) {
        postFlowpane.getChildren().clear(); // Clear previous posts

        // Apply custom styles to the FlowPane
        postFlowpane.setStyle("-fx-background-color: #272926; -fx-padding: 20; -fx-hgap: 15; -fx-vgap: 15;");

        String saleOrRentCondition = "";
        String negotiableCondition = "";
        String locationSearchCondition = "AND Posts.location LIKE ?"; // Condition for searching by location

        // Check if 'sale' or 'rent' is selected and add filtering accordingly
        if (sale.isSelected()) {
            saleOrRentCondition = "AND Posts.sale_or_rent = 'Sale'";
        } else if (rent.isSelected()) {
            saleOrRentCondition = "AND Posts.sale_or_rent = 'Rent'";
        }

        // Check if 'negotiable' or 'non-negotiable' is selected and add filtering
        if (negotiable.isSelected()) {
            negotiableCondition = "AND Posts.negotiable = 'Negotiable'";
        } else if (non_negotiable.isSelected()) {
            negotiableCondition = "AND Posts.negotiable = 'Non-Negotiable'";
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Query to fetch the posts based on location (areaName) and any selected filters
            String query = """
        SELECT 
            Posts.post_id AS postId,
            Users.name AS username, 
            Posts.created_at, 
            Posts.title, 
            Posts.template_path,
            Posts.location
        FROM Posts
        JOIN Users ON Posts.user_id = Users.id
        WHERE 1 = 1
        """ + saleOrRentCondition + " " + negotiableCondition + " " + locationSearchCondition + """
        ORDER BY Posts.created_at DESC
        """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + areaName + "%");  // Use LIKE for partial matching of the area name
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int postId = rs.getInt("postId");
                String dbUsername = rs.getString("username");
                String createdAt = rs.getString("created_at");
                String title = rs.getString("title");
                String templatePath = rs.getString("template_path");
                String location = rs.getString("location");

                // Query to get reaction counts (likes and dislikes)
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

                // Create a VBox for the post
                VBox postBox = new VBox(10);
                postBox.setStyle("""
            -fx-padding: 15;
            -fx-border-color: #dcdcdc;
            -fx-border-width: 1;
            -fx-border-radius: 10;
            -fx-background-color: #ffffff;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);
            -fx-pref-width: 280px; /* Decreased width */
            -fx-pref-height: 400px;
            """);

                // Add the username label
                Label usernameLabel = new Label("Posted by: " + dbUsername);
                usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

                // Add the creation time label
                Label createdAtLabel = new Label("Posted on: " + createdAt);
                createdAtLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                // Add the title label
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #34495e;");

                // Add the location label
                Label locationLabel = new Label("Location: " + location);
                locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

                // Add the template image if available
                ImageView templateImageView = null;
                if (templatePath != null && !templatePath.isEmpty()) {
                    templateImageView = new ImageView(new Image(templatePath));
                    templateImageView.setFitWidth(260);
                    templateImageView.setFitHeight(200);
                    templateImageView.setPreserveRatio(false);
                    templateImageView.setStyle("-fx-border-color: #dcdcdc; -fx-border-width: 1; -fx-border-radius: 5;");
                }

                // Create a HBox for reactions and "More Options" (3 dots)
                HBox reactionBox = new HBox(10);
                reactionBox.setStyle("-fx-alignment: center-left;"); // Align to the left

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

                // Create the "Details" MenuItem
                MenuItem detailsItem = new MenuItem("Details");
                detailsItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;");
                detailsItem.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("postDetails.fxml"));
                        Scene postDetailsScene = new Scene(loader.load());
                        PostDetailsController controller = loader.getController();
                        controller.setPostId(postId);
                        Stage stage = (Stage) postFlowpane.getScene().getWindow();
                        stage.setScene(postDetailsScene);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // Create the "Comment" MenuItem
                MenuItem commentItem = new MenuItem("Comment");
                commentItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");
                commentItem.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("postComment.fxml"));
                        Scene postCommentScene = new Scene(loader.load());
                        PostCommentController controller = loader.getController();
                        controller.setPostId(postId);  // Pass the postId to the PostCommentController
                        controller.loadComments(postId);  // Load comments for this post
                        Stage stage = (Stage) postFlowpane.getScene().getWindow();
                        stage.setScene(postCommentScene);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // Add the "Details" and "Comment" options to the MenuButton
                moreOptionsButton.getItems().addAll(detailsItem, commentItem);

                // Add reactions, counts, and "More Options" button to the reactionBox
                reactionBox.getChildren().addAll(thumbsUpButton, likeCountLabel, thumbsDownButton, dislikeCountLabel, moreOptionsButton);

                // Add all components to the VBox
                postBox.getChildren().addAll(usernameLabel, createdAtLabel, titleLabel, locationLabel);
                if (templateImageView != null) {
                    postBox.getChildren().add(templateImageView);
                }
                postBox.getChildren().add(reactionBox);

                // Add the postBox to the FlowPane
                postFlowpane.getChildren().add(postBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
