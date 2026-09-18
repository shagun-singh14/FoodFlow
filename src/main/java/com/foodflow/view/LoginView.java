package com.foodflow.view;

import com.foodflow.MainApp;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.User;
import com.foodflow.model.enums.UserRole;
import com.foodflow.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Modern Glassmorphic Login Screen for FoodFlow.
 */
public class LoginView {

    private final AuthService authService;
    private final MainApp mainApp;

    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = new AuthService();
    }

    public Pane getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-window");

        VBox centerBox = new VBox(24);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setMaxWidth(440);
        centerBox.setPadding(new Insets(30));

        // Header Branding
        VBox brandBox = new VBox(8);
        brandBox.setAlignment(Pos.CENTER);

        Label title = new Label("FoodFlow");
        title.getStyleClass().add("heading-1");
        title.setStyle("-fx-font-size: 32px; -fx-text-fill: #818cf8;");

        Label subtitle = new Label("College Mess Management Portal • VIT");
        subtitle.getStyleClass().add("text-muted");

        brandBox.getChildren().addAll(title, subtitle);

        // Glass Form Card
        VBox card = new VBox(16);
        card.getStyleClass().add("glass-card");

        Label formTitle = new Label("Sign In to Your Account");
        formTitle.getStyleClass().add("heading-3");

        Label emailLbl = new Label("Institutional Email");
        emailLbl.getStyleClass().add("kpi-label");
        TextField emailField = new TextField();
        emailField.setPromptText("e.g. rahul.sharma@vit.ac.in");

        Label passLbl = new Label("Password");
        passLbl.getStyleClass().add("kpi-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");

        Button loginBtn = UIComponents.createPrimaryButton("Sign In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Label statusLbl = new Label();
        statusLbl.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");
        statusLbl.setWrapText(true);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();
            try {
                User user = authService.login(email, password);
                if (user.getRole() == UserRole.STUDENT) {
                    mainApp.showStudentDashboard();
                } else {
                    mainApp.showAdminDashboard();
                }
            } catch (FoodFlowException ex) {
                statusLbl.setText(ex.getMessage());
            } catch (Exception ex) {
                statusLbl.setText("Connection or authentication error: " + ex.getMessage());
            }
        });

        // Quick Demo Credentials Buttons
        Label demoLbl = new Label("Quick Demo Logins:");
        demoLbl.getStyleClass().add("text-muted");

        HBox demoBtnBox = new HBox(10);
        demoBtnBox.setAlignment(Pos.CENTER);

        Button studentDemoBtn = new Button("👤 Student (Rahul)");
        studentDemoBtn.setOnAction(e -> {
            emailField.setText("rahul.sharma@vit.ac.in");
            passField.setText("Password@123");
            loginBtn.fire();
        });

        Button adminDemoBtn = new Button("🛡️ Admin (Prof. Menon)");
        adminDemoBtn.setOnAction(e -> {
            emailField.setText("admin@foodflow.edu");
            passField.setText("Password@123");
            loginBtn.fire();
        });

        demoBtnBox.getChildren().addAll(studentDemoBtn, adminDemoBtn);

        // Register Link
        HBox registerBox = new HBox(8);
        registerBox.setAlignment(Pos.CENTER);
        Label noAccLbl = new Label("Don't have an account?");
        noAccLbl.getStyleClass().add("text-muted");
        Hyperlink registerLink = new Hyperlink("Register Here");
        registerLink.setStyle("-fx-text-fill: #818cf8; -fx-font-weight: bold;");
        registerLink.setOnAction(e -> mainApp.showRegisterView());
        registerBox.getChildren().addAll(noAccLbl, registerLink);

        card.getChildren().addAll(formTitle, emailLbl, emailField, passLbl, passField, loginBtn, statusLbl, new Separator(), demoLbl, demoBtnBox);
        centerBox.getChildren().addAll(brandBox, card, registerBox);
        root.setCenter(centerBox);

        return root;
    }
}
