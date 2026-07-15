package com.rabitah.frontend;

import static org.junit.jupiter.api.Assertions.*;

import com.rabitah.frontend.controller.LoginController;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FrontendViewTest {
    private static AppContext context;

    @BeforeAll
    static void startJavaFx() throws Exception {
        context = new AppContext();
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        assertTrue(started.await(5, TimeUnit.SECONDS));
    }

    @AfterAll
    static void stopServices() {
        context.close();
        Platform.exit();
    }

    @Test
    void passwordAcceptsInputAndCanBeRevealed() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = loader("login.fxml", new LoginController(context));
            Parent root = loader.load();
            PasswordField masked = (PasswordField) root.lookup("#password");
            TextField visible = (TextField) root.lookup("#visiblePassword");
            CheckBox show = (CheckBox) root.lookup("#showPassword");

            assertNotNull(masked);
            assertTrue(masked.isEditable());
            masked.setText("Rabitah123!");
            assertEquals("Rabitah123!", masked.getText());
            assertEquals(masked.getText(), visible.getText());
            show.fire();
            assertTrue(visible.isVisible());
            assertFalse(masked.isVisible());
            assertEquals("Rabitah123!", visible.getText());
        });
    }

    private FXMLLoader loader(String name, Object controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rabitah/frontend/fxml/" + name));
        loader.setControllerFactory(type -> controller);
        return loader;
    }

    private void runOnJavaFxThread(ThrowingRunnable action) throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable error) {
                failure.set(error);
            } finally {
                completed.countDown();
            }
        });
        assertTrue(completed.await(5, TimeUnit.SECONDS));
        if (failure.get() != null) fail(failure.get());
    }

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }
}
