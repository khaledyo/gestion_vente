package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.ConnectToDB;

public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField enterPasswordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {

        usernameTextField.setFocusTraversable(false);
        enterPasswordField.setFocusTraversable(false);
        
        loginButton.setOnAction(event -> {
            removeTextSelection();
            login();
        });
        enterPasswordField.setOnAction(event -> {
            removeTextSelection();
            login();
        });
    }

    private void removeTextSelection() {
        Platform.runLater(() -> {
            usernameTextField.positionCaret(0);
            enterPasswordField.positionCaret(0);
            loginButton.requestFocus();
        });
    }

    private void login() {
        String username = usernameTextField.getText();
        String password = enterPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
        setFieldsEditable(false);
        loginButton.setDisable(true);
        loginButton.setText("Processing...");

        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                try (Connection conn = ConnectToDB.getConnection()) {
                    String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, password);

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String role = rs.getString("role");
                        if ("ADMIN".equals(role)) {
                            Platform.runLater(() -> openPage("/fxml/Main.fxml"));
                        } 
                    } else {
                        Platform.runLater(() -> showError("Invalid username or password."));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Database connection error."));
                    e.printStackTrace();
                }
                return null;
            }
        };

        loginTask.setOnSucceeded(e -> {
            setFieldsEditable(true);
            resetLoginButton();
        });

        loginTask.setOnFailed(e -> {
            setFieldsEditable(true);
            resetLoginButton();
        });

        new Thread(loginTask).start();
    }

    private void setFieldsEditable(boolean editable) {
        Platform.runLater(() -> {
            usernameTextField.setEditable(editable);
            enterPasswordField.setEditable(editable);
            if (editable) {
                usernameTextField.requestFocus();
            }
        });
    }

    private void resetLoginButton() {
        Platform.runLater(() -> {
            loginButton.setDisable(false);
            loginButton.setText("Login");
        });
    }

    private void openPage(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            showError("Failed to load page.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setVisible(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> errorLabel.setVisible(false));
            pause.play();
        });
    }
}