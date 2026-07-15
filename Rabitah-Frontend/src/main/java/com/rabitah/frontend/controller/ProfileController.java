package com.rabitah.frontend.controller;

import com.rabitah.frontend.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public final class ProfileController {
    private final AppContext context;

    @FXML private Label nicknameValue;
    @FXML private Label loginIdValue;
    @FXML private Label studentIdValue;
    @FXML private Label roleValue;
    @FXML private Label departmentValue;
    @FXML private Label sectionValue;
    @FXML private Label yearValue;

    public ProfileController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        var current = context.session().current();
        if (current == null) {
            context.router().showLogin();
            return;
        }
        var user = current.user();
        nicknameValue.setText(user.nickname());
        loginIdValue.setText(user.loginId());
        studentIdValue.setText(value(user.studentId()));
        roleValue.setText(user.role().replace('_', ' '));
        departmentValue.setText(value(user.department()));
        sectionValue.setText(value(user.section()));
        yearValue.setText(value(user.academicYear()));
    }

    @FXML
    private void back() {
        context.router().showShell();
    }

    private String value(Object value) {
        return value == null ? "Not applicable" : value.toString();
    }
}
