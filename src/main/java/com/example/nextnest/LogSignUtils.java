package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogSignUtils {

    public static String loggedInUserId = null;

    // Change scene utility method
    public static void changeScene(ActionEvent event, String fxmlFile) {
        Parent root = null;

        try {
            root = FXMLLoader.load(LogSignUtils.class.getResource(fxmlFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Validate user login credentials
    public static void loginUser(ActionEvent event, User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = "SELECT id, password FROM users WHERE mobile_number = ? AND password = ?";

        try {
            connection = DatabaseUtil.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getNumber());
            preparedStatement.setString(2, user.getPassword());

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found in database!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image("img.png"));


                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #f0f8ff; -fx-text-fill: #000000;");
                dialogPane.lookup(".header-panel").setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");
                dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-font-weight: bold; -fx-background-color: #6b1d1d; -fx-text-fill: #ffffff;");
            } else {
                resultSet.next();
                String userId = resultSet.getString("id");

                // Set the logged-in user ID
                loggedInUserId = userId;

                // Change to the home scene
                changeScene(event, "home.fxml");
                System.out.println("User logged in successfully with id " + loggedInUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // Register a new user
    public static void SignUpUser(ActionEvent event, User user) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheck = null;
        ResultSet resultSet = null;
        String query = "INSERT INTO users (name, mobile_number, gender, email, address, nid, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {

            connection = DatabaseUtil.getConnection();

            psCheck = connection.prepareStatement("Select * From users Where mobile_number =?");
            psCheck.setString(1, user.getNumber());
            resultSet = psCheck.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("User with this number is already exist!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("This number has an account!");
                alert.setContentText("Choose a different number.");
                alert.show();

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image("img.png"));


                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #f0f8ff; -fx-text-fill: #000000;");
                dialogPane.lookup(".header-panel").setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");
                dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-font-weight: bold; -fx-background-color: #6b1d1d; -fx-text-fill: #ffffff;");
            } else {

                psInsert = connection.prepareStatement(query);
                psInsert.setString(1, user.getName());
                psInsert.setString(2, user.getNumber());
                psInsert.setString(3, user.getGender());
                psInsert.setString(4, user.getEmail());
                psInsert.setString(5, user.getAddress());
                psInsert.setString(6, user.getNid());
                psInsert.setString(7, user.getPassword());
                psInsert.executeUpdate();

                changeScene(event, "login.fxml");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, psCheck, psInsert, connection);
        }

    }

    //remember me validation  method
    public static boolean validateLogin(User user) {
        String query = "SELECT id FROM users WHERE mobile_number = ? AND password = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNumber());
            preparedStatement.setString(2, user.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                loggedInUserId = resultSet.getString("id");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Utility method to close resources
    public static void closeResources(ResultSet rs, PreparedStatement ps, PreparedStatement ps1, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (ps1 != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}