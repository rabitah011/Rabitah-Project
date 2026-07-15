package com.rabitah.frontend.controller;

import com.rabitah.frontend.AppContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public final class LoginController {
    private final AppContext context;

    @FXML private TextField loginId;
    @FXML private PasswordField password;
    @FXML private TextField visiblePassword;
    @FXML private CheckBox showPassword;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

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
        errorLabel.setText("");
        String id = loginId.getText().trim();
        String enteredPassword = password.getText();
        if (id.isBlank() || enteredPassword.isBlank()) {
            errorLabel.setText("Enter both your system/student ID and password.");
            return;
        }
        loginButton.setDisable(true);
        context.auth().login(id, enteredPassword).whenComplete((response, error) ->
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    if (error != null) {
                        Throwable cause = error.getCause() == null ? error : error.getCause();
                        errorLabel.setText(cause.getMessage());
                        return;
                    }
                    context.session().open(response);
                    context.router().showShell();
                }));
    }
}
