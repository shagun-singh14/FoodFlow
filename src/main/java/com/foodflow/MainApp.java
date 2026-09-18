package com.foodflow;

import com.foodflow.config.DatabaseManager;
import com.foodflow.config.JPAUtil;
import com.foodflow.util.LoggerUtil;
import com.foodflow.view.AdminDashboardView;
import com.foodflow.view.LoginView;
import com.foodflow.view.RegisterView;
import com.foodflow.view.StudentDashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Desktop Application Entry Point for FoodFlow Mess Management System.
 */
public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("FoodFlow - College Mess Management System");
        this.primaryStage.setMinWidth(1100);
        this.primaryStage.setMinHeight(720);

        // Pre-initialize Singleton Database and Logger
        LoggerUtil.info(MainApp.class, "Launching FoodFlow UI Application...");
        DatabaseManager.getInstance();

        showLoginView();
        stage.show();
    }

    public void showLoginView() {
        LoginView view = new LoginView(this);
        Scene scene = new Scene(view.getView(), 1150, 750);
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    public void showRegisterView() {
        RegisterView view = new RegisterView(this);
        Scene scene = new Scene(view.getView(), 1150, 750);
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    public void showStudentDashboard() {
        StudentDashboardView view = new StudentDashboardView(this);
        Scene scene = new Scene(view.getView(), 1150, 750);
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    public void showAdminDashboard() {
        AdminDashboardView view = new AdminDashboardView(this);
        Scene scene = new Scene(view.getView(), 1150, 750);
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    private void applyStyles(Scene scene) {
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            LoggerUtil.warning(MainApp.class, "Could not load CSS stylesheet: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        LoggerUtil.info(MainApp.class, "Closing FoodFlow Application...");
        JPAUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
