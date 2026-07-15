package com.rabitah.frontend.controller;
import com.rabitah.frontend.AppContext; import javafx.fxml.FXML; import javafx.scene.control.*;
public final class ShellController {private final AppContext context;@FXML private Label welcome;public ShellController(AppContext context){this.context=context;}@FXML private void initialize(){var u=context.session().current().user();welcome.setText("Welcome, "+u.nickname()+" ("+u.studentId()+")");}@FXML private void logout(){context.session().clear();context.router().showLogin();}}
