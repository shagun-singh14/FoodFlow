package com.foodflow.view;

import com.foodflow.MainApp;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Modern Student Registration Screen.
 */
public class RegisterView {

    private final AuthService authService;
    private final MainApp mainApp;

    public RegisterView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = new AuthService();
    }

    public Pane getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-window");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setMaxWidth(520);
        centerBox.setPadding(new Insets(30));

        // Header Branding
        VBox brandBox = new VBox(4);
        brandBox.setAlignment(Pos.CENTER);
        Label title = new Label("Student Registration");
        title.getStyleClass().add("heading-1");
        Label subtitle = new Label("Enroll in FoodFlow College Mess Portal");
        subtitle.getStyleClass().add("text-muted");
        brandBox.getChildren().addAll(title, subtitle);

        // Form Card
        VBox card = new VBox(14);
        card.getStyleClass().add("glass-card");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email (e.g. name@vit.ac.in)");

        TextField phoneField = new TextField();
        phoneField.setPromptText("10-Digit Phone Number");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password (min 6 characters)");

        TextField regNoField = new TextField();
        regNoField.setPromptText("Reg No (e.g. 23BCE1234)");

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.getItems().addAll("B.Tech CSE", "B.Tech IT", "B.Tech ECE", "B.Tech Mech", "B.Tech Data Sci", "B.Tech EEE");
        courseBox.setValue("B.Tech CSE");
        courseBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Integer> yearBox = new ComboBox<>();
        yearBox.getItems().addAll(1, 2, 3, 4);
        yearBox.setValue(1);
        yearBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> hostelBox = new ComboBox<>();
        hostelBox.getItems().addAll("Block A - Mens Hostel", "Block B - Mens Hostel", "Block C - Mens Hostel",
                "Block D - Ladies Hostel", "Block E - Ladies Hostel");
        hostelBox.setValue("Block A - Mens Hostel");
        hostelBox.setMaxWidth(Double.MAX_VALUE);

        TextField roomField = new TextField();
        roomField.setPromptText("Room Number (e.g. A-304)");

        Button registerBtn = UIComponents.createSuccessButton("Complete Registration");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        Label statusLbl = new Label();
        statusLbl.setWrapText(true);

        registerBtn.setOnAction(e -> {
            try {
                authService.registerStudent(
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        passField.getText(),
                        regNoField.getText(),
                        courseBox.getValue(),
                        yearBox.getValue(),
                        hostelBox.getValue(),
                        roomField.getText()
                );
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Welcome to FoodFlow!",
                        "Your account has been created. You can now login with your credentials.");
                mainApp.showLoginView();
            } catch (FoodFlowException ex) {
                statusLbl.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");
                statusLbl.setText(ex.getMessage());
            } catch (Exception ex) {
                statusLbl.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");
                statusLbl.setText("Registration error: " + ex.getMessage());
            }
        });

        Button backBtn = new Button("← Back to Login");
        backBtn.setOnAction(e -> mainApp.showLoginView());

        card.getChildren().addAll(
                new Label("Personal Information"),
                nameField, emailField, phoneField, passField,
                new Separator(),
                new Label("Academic & Hostel Information"),
                regNoField, courseBox, yearBox, hostelBox, roomField,
                registerBtn, statusLbl, backBtn
        );

        centerBox.getChildren().addAll(brandBox, card);
        scrollPane.setContent(centerBox);
        root.setCenter(scrollPane);

        return root;
    }
}
