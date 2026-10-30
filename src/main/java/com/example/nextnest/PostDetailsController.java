package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostDetailsController {

    Scene scene;
    Scene s3;
    Scene s4;
    Scene s5;
    Scene s6;
    Scene s7;
    Scene s8;
    Stage stage;
    Scene s2;
    @FXML
    private ImageView imageView;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label price;
    @FXML
    private Label lcn;
    @FXML
    private Label house_no;
    @FXML
    private Label mobile_no;
    @FXML
    private Label email;
    @FXML
    private Label sale;
    @FXML
    private Label negotiable;
    private int postId;
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container

    public void setPostId(int postId) {
        this.postId = postId;
        loadPostDetails(); // Load post details whenever the postId is set
    }

    private void loadPostDetails() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                    SELECT title,
                     description, price, location, house_no, phone_number, email, sale_or_rent, negotiable, 
                           image_path
                    FROM Posts WHERE post_id = ?""";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, postId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Set the post details in labels
                title.setText(rs.getString("title"));
                description.setText(rs.getString("description"));
                price.setText("Price: " + rs.getBigDecimal("price"));
                lcn.setText(rs.getString("location"));
                house_no.setText(rs.getString("house_no"));
                mobile_no.setText(rs.getString("phone_number"));
                email.setText(rs.getString("email"));
                sale.setText("For: " + rs.getString("sale_or_rent"));
                negotiable.setText(rs.getString("negotiable"));

                // Load and display the primary image
                String imagePath = rs.getString("image_path");
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageView.setImage(new Image(imagePath));
                    imageView.setPreserveRatio(false);
                }


            } else {
                // Show error if the post is not found
                title.setText("Post not found");
                description.setText("No post found!");
                price.setText("");
                lcn.setText("");
                house_no.setText("");
                mobile_no.setText("");
                email.setText("");
                sale.setText("");
                negotiable.setText("");
                imageView.setImage(null);

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

    @FXML
    public void initialize() {
        // Observe theme changes and apply them to both root panes
        ThemeManager.getInstance().currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            applyTheme(rootPane1, newValue);
            applyTheme(rootPane2, newValue);
        });

        // Set the initial theme
        applyTheme(rootPane1, ThemeManager.getInstance().getCurrentTheme());
        applyTheme(rootPane2, ThemeManager.getInstance().getCurrentTheme());
    }

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


}
