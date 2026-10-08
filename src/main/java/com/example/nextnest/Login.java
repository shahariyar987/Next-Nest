package com.example.nextnest;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.prefs.Preferences;

public class Login {

    private final Preferences preferences = Preferences.userNodeForPackage(Login.class);

    @FXML
    private TextField t1; // Mobile Number
    @FXML
    private PasswordField p1; // Password
    @FXML
    private CheckBox remember_me; // "Remember Me" checkbox
    @FXML
    private ProgressBar progressBar; // Progress bar for animation
    private Timeline progressTimeline;

    @FXML
    public void initialize() {
        // Load the remembered phone number if available
        String rememberedPhone = preferences.get("rememberedPhone", "");
        if (!rememberedPhone.isEmpty()) {
            t1.setText(rememberedPhone);
            remember_me.setSelected(true);
        }

        progressBar.setProgress(0); // Ensure progress bar starts at 0

        // Load and apply the CSS file
        String cssPath = getClass().getResource("progressbar.css").toExternalForm(); // Adjust the path if needed
        if (progressBar.getScene() != null) { // Check if the scene is available
            progressBar.getScene().getStylesheets().add(cssPath);
        }
    }

    @FXML
    public void shiftScene(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    @FXML
    public void login(ActionEvent e) {
        String mobileNumber = t1.getText();
        String password = p1.getText();

        if (mobileNumber.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in both mobile number and password.", Alert.AlertType.ERROR);
            return;
        }

        User user = new User(mobileNumber, password);

        // Start the progress bar animation
        startProgressBar();

        // Simulate a login process (database validation, etc.)
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate login delay (replace with actual validation)
                boolean isValid = LogSignUtils.validateLogin(user);

                javafx.application.Platform.runLater(() -> {
                    stopProgressBar(); // Stop the progress bar animation

                    if (isValid) {
                        // Save the phone number if "Remember Me" is selected
                        if (remember_me.isSelected()) {
                            preferences.put("rememberedPhone", mobileNumber);
                        } else {
                            preferences.remove("rememberedPhone");
                        }

                        // Proceed to the next scene
                        LogSignUtils.changeScene(e, "home.fxml");
                    } else {
                        showAlert("Error", "Invalid credentials. Please try again.", Alert.AlertType.ERROR);
                    }
                });
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // Start progress bar animation
    private void startProgressBar() {
        progressBar.setProgress(0); // Reset progress bar

        progressBar.setStyle("-fx-accent: #151928;");

        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        glow.setColor(Color.CORNFLOWERBLUE);
        glow.setWidth(20);
        glow.setHeight(20);
        progressBar.setEffect(glow);

        progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 1)) // Fill in 2 seconds
        );

        progressTimeline.setCycleCount(1); // Run the animation once
        progressTimeline.play();

        // Remove the glow after the animation
        progressTimeline.setOnFinished(event -> progressBar.setEffect(null));
    }

    // Stop progress bar animation
    private void stopProgressBar() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
        progressBar.setProgress(0); // Reset progress bar
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
