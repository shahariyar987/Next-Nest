package com.example.nextnest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HelloApplication extends Application {

    private Thread serverThread; // Thread to run the server
    private volatile boolean running = true; // Control flag for server shutdown

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Set the title and other properties
        stage.setTitle("nextNest?");
        stage.setScene(scene);
        stage.setResizable(false);

        // Set the application icon
        Image iconImage = new Image("logo bgr.png");
        stage.getIcons().add(iconImage);

        // Add stylesheet
        scene.getStylesheets().add(getClass().getResource("progressbar.css").toExternalForm());

        // Calculate screen bounds and center the stage
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double stageWidth = 1292; // Your window width
        double stageHeight = 980; // Your window height
        double centerX = (screenBounds.getWidth() - stageWidth) / 2;
        double centerY = (screenBounds.getHeight() - stageHeight) / 2;

        // Set the stage dimensions and position
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setX(centerX);
        stage.setY(centerY);

        // Show the stage
        stage.show();

        // Start the server thread
        startServer();
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) { // Replace with your desired port
                System.out.println("Server started on port 12345...");

                while (running) {
                    // Accept connections
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Handle client in a separate thread (or process the request here)
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                if (running) { // Log only if the server wasn't stopped intentionally
                    System.err.println("Server error: " + e.getMessage());
                }
            }
        });

        serverThread.setDaemon(true); // Ensure server thread shuts down with the application
        serverThread.start();
    }

    @Override
    public void stop() {
        // Stop the server when the application exits
        running = false;
        try {
            if (serverThread != null && serverThread.isAlive()) {
                serverThread.join(); // Wait for the server thread to finish
            }
        } catch (InterruptedException e) {
            System.err.println("Error stopping server thread: " + e.getMessage());
        }

        System.out.println("Application exited. Server stopped.");
    }

    // Example client handler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Example: Communicate with the client
                System.out.println("Handling client: " + clientSocket.getInetAddress());
                // Add your chat/messenger logic here
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            }
        }
    }
}

