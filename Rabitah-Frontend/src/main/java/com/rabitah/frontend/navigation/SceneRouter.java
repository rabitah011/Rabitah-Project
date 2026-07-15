package com.rabitah.frontend.navigation;

import com.rabitah.frontend.AppContext;
import com.rabitah.frontend.controller.LoginController;
import com.rabitah.frontend.controller.ProfileController;
import com.rabitah.frontend.controller.ShellController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class SceneRouter {
    private final AppContext context;
    private Stage stage;
    private Scene scene;

    public SceneRouter(AppContext context) { this.context = context; }

    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Rabitah — campus platform build 2.1");
        stage.setMinWidth(860);
        stage.setMinHeight(540);

        Rectangle2D desktop = Screen.getPrimary().getVisualBounds();
        double width = Math.min(1180, desktop.getWidth() * 0.92);
        double height = Math.min(700, desktop.getHeight() * 0.88);
        scene = new Scene(load("login.fxml", LoginController.class), width, height);
        scene.getStylesheets().add(getClass().getResource("/com/rabitah/frontend/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void showLogin() { replace("login.fxml", LoginController.class); }
    public void showShell() { replace("shell.fxml", ShellController.class); }
    public void showProfile() { replace("profile.fxml", ProfileController.class); }

    private void replace(String name, Class<?> controllerType) {
        if (scene == null) throw new IllegalStateException("SceneRouter has not been started");
        scene.setRoot(load(name, controllerType));
    }

    private Parent load(String name, Class<?> controllerType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rabitah/frontend/fxml/" + name));
            loader.setControllerFactory(type -> {
                if (type != controllerType) throw new IllegalArgumentException("Unsupported controller: " + type);
                if (type == LoginController.class) return new LoginController(context);
                if (type == ShellController.class) return new ShellController(context);
                if (type == ProfileController.class) return new ProfileController(context);
                throw new IllegalArgumentException("Unsupported controller: " + type);
            });
            return loader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load " + name, error);
        }
    }
}
