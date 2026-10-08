package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class signupController implements Initializable {

    @FXML
    private TextField t1; // Name
    @FXML
    private TextField t2; // Mobile Number
    @FXML
    private TextField t3; // Email
    @FXML
    private TextField t4; // Address
    @FXML
    private TextField t5; // NID
    @FXML
    private TextField t6; // Password
    @FXML
    private RadioButton Male;
    @FXML
    private RadioButton Female;
    @FXML
    private RadioButton Others;
    @FXML
    private Button signup_btn;


    @FXML
    public void goToLog(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = new User();

        signup_btn.setOnAction(actionEvent -> {
            if (!t1.getText().isEmpty() && !t2.getText().isEmpty() && !t3.getText().isEmpty() && !t4.getText().isEmpty() && !t5.getText().isEmpty() && !t6.getText().isEmpty()) {

                // Get the selected gender value
                String gender = null;
                if (Male.isSelected()) {
                    gender = "Male";
                } else if (Female.isSelected()) {
                    gender = "Female";
                } else if (Others.isSelected()) {
                    gender = "Others";
                }

                if (gender == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please select a gender!");
                    alert.show();
                    return;
                }

                // Populate the User object
                user.setName(t1.getText());
                user.setNumber(t2.getText());
                user.setEmail(t3.getText());
                user.setAddress(t4.getText());
                user.setNid(t5.getText());
                user.setPassword(t6.getText());
                user.setGender(gender);

                // Call the signup method
                LogSignUtils.SignUpUser(actionEvent, user);
            } else {
                System.out.println("Please fill all the information.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill all the information!");
                alert.show();

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image("img.png"));


                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #f0f8ff; -fx-text-fill: #000000;");
                dialogPane.lookup(".header-panel").setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");
                dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-font-weight: bold; -fx-background-color: #6b1d1d; -fx-text-fill: #ffffff;");
                //dialogPane.lookupButton(ButtonType.CANCEL).setStyle("-fx-font-weight: bold; -fx-background-color: #FF6F61; -fx-text-fill: #ffffff;");
            }
        });
    }


}

