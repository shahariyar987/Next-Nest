package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostCommentController {

    @FXML
    private VBox commentVBox; // VBox to hold comments dynamically
    @FXML
    private TextField commentInputField; // Input field for new comment
    @FXML
    private Button submitCommentButton; // Button to submit comment

    private int postId; // Post ID for which we are adding or viewing comments

    // This method is called when loading comments
    public void loadComments(int postId) {
        commentVBox.getChildren().clear(); // Clear previous comments

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                        SELECT 
                            Users.name AS username, 
                            Comments.comment_text AS commentText, 
                            Comments.created_at AS createdAt 
                        FROM Comments 
                        JOIN Users ON Comments.user_id = Users.id 
                        WHERE Comments.post_id = ?
                        ORDER BY Comments.created_at ASC
                    """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            int index = 0; // Keep track of the comment index

            while (rs.next()) {
                String username = rs.getString("username");
                String commentText = rs.getString("commentText");
                String createdAt = rs.getString("createdAt");

                // Create a VBox for the comment
                VBox commentBox = new VBox(5);
                commentBox.setStyle("""
                            -fx-padding: 10;
                            -fx-border-radius: 5;
                            -fx-background-radius: 5;
                            -fx-pref-width: 400px;
                            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
                        """);

                // Alternate background color based on index
                if (index % 2 == 0) {
                    commentBox.setStyle(commentBox.getStyle() + "-fx-background-color: #f5f5f5;");
                } else {
                    commentBox.setStyle(commentBox.getStyle() + "-fx-background-color: #dcdcdc;");
                }

                // Add username
                Label usernameLabel = new Label(username);
                usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                // Add creation time
                Label createdAtLabel = new Label("Posted on: " + createdAt);
                createdAtLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                // Add the comment text
                Label commentTextLabel = new Label(commentText);
                commentTextLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-wrap-text: true;");

                // Add all components to the commentBox
                commentBox.getChildren().addAll(usernameLabel, createdAtLabel, commentTextLabel);

                // Add the commentBox to the VBox
                commentVBox.getChildren().add(commentBox);

                index++; // Increment the index for alternating style
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // This method is called when the "Submit Comment" button is clicked
    @FXML
    private void submitComment() {
        String newComment = commentInputField.getText().trim();

        if (newComment.isEmpty()) {
            return; // Do not submit empty comments
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get the currently logged-in user's ID
            int userId = Integer.parseInt(LogSignUtils.loggedInUserId);

            // Insert the new comment into the Comments table
            String insertQuery = "INSERT INTO Comments (post_id, user_id, comment_text) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, postId); // Post ID
            insertStmt.setInt(2, Integer.parseInt(LogSignUtils.loggedInUserId)); // Logged-in user ID
            insertStmt.setString(3, newComment); // Comment text
            insertStmt.executeUpdate();

            // Reload the comments after inserting
            loadComments(postId);
            commentInputField.clear(); // Clear the input field after submitting
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setPostId(int postId) {
        this.postId = postId;
    }

    @FXML
    private void goToHome(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
