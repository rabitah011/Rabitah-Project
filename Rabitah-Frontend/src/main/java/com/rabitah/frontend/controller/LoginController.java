package com.rabitah.frontend.controller;

import com.rabitah.frontend.AppContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public final class LoginController {
    private final AppContext context;

    @FXML private TextField loginId;
    @FXML private PasswordField password;
    @FXML private TextField visiblePassword;
    @FXML private CheckBox showPassword;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    private Timeline approvalPolling;
    private String waitingId;
    private String waitingPassword;
    private boolean requestRunning;

    public LoginController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        visiblePassword.textProperty().bindBidirectional(password.textProperty());
        Platform.runLater(loginId::requestFocus);
    }

    @FXML
    private void togglePasswordVisibility() {
        boolean show = showPassword.isSelected();
        visiblePassword.setVisible(show);
        visiblePassword.setManaged(show);
        password.setVisible(!show);
        password.setManaged(!show);
        TextField activeField = show ? visiblePassword : password;
        activeField.requestFocus();
        activeField.positionCaret(activeField.getText().length());
    }

    @FXML
    private void login() {
        stopPolling();
        errorLabel.setText("");
        String id = loginId.getText().trim();
        String enteredPassword = password.getText();
        if (id.isBlank() || enteredPassword.isBlank()) {
            errorLabel.setText("Enter both your system/student ID and password.");
            return;
        }
        attemptLogin(id, enteredPassword, true);
    }

    private void attemptLogin(String id, String enteredPassword, boolean startWaiting) {
        if (requestRunning) return;
        requestRunning = true;
        loginButton.setDisable(true);
        context.auth().login(id, enteredPassword).whenComplete((response, error) ->
                Platform.runLater(() -> {
                    requestRunning = false;
                    if (error != null) {
                        Throwable cause = error.getCause() == null ? error : error.getCause();
                        String message = cause.getMessage() == null ? "Sign-in failed" : cause.getMessage();
                        String lower = message.toLowerCase();
                        if (lower.contains("approval") || lower.contains("sent to the system administrator") || lower.contains("waiting for")) {
                            errorLabel.setText("Waiting for SysAdmin approval... You will enter automatically once approved.");
                            if (startWaiting || approvalPolling == null) startPolling(id, enteredPassword);
                        } else {
                            stopPolling();
                            loginButton.setDisable(false);
                            errorLabel.setText(message);
                        }
                        return;
                    }
                    stopPolling();
                    context.session().open(response);
                    context.router().showShell();
                }));
    }

    private void startPolling(String id, String enteredPassword) {
        waitingId = id;
        waitingPassword = enteredPassword;
        loginId.setDisable(true);
        password.setDisable(true);
        visiblePassword.setDisable(true);
        showPassword.setDisable(true);
        loginButton.setText("Waiting for approval...");
        approvalPolling = new Timeline(new KeyFrame(Duration.seconds(2), event -> attemptLogin(waitingId, waitingPassword, false)));
        approvalPolling.setCycleCount(Timeline.INDEFINITE);
        approvalPolling.play();
    }

    private void stopPolling() {
        if (approvalPolling != null) approvalPolling.stop();
        approvalPolling = null;
        loginId.setDisable(false);
        password.setDisable(false);
        visiblePassword.setDisable(false);
        showPassword.setDisable(false);
        loginButton.setDisable(false);
        loginButton.setText("Sign in / Request access");
    }
}
