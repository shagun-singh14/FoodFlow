package com.foodflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Optional;

/**
 * Reusable JavaFX UI helper generating modern glassmorphic widgets, badges, cards, and dialogs.
 */
public class UIComponents {

    public static VBox createCard(String title, Node... content) {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");

        if (title != null && !title.isEmpty()) {
            Label header = new Label(title);
            header.getStyleClass().add("heading-3");
            card.getChildren().add(header);
        }

        card.getChildren().addAll(content);
        return card;
    }

    public static VBox createKpiCard(String label, String value, String subtext, String colorHex) {
        VBox card = new VBox(6);
        card.getStyleClass().add("kpi-card");
        card.setMinWidth(170);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("kpi-label");

        Label val = new Label(value);
        val.getStyleClass().add("kpi-value");
        if (colorHex != null) {
            val.setStyle("-fx-text-fill: " + colorHex + ";");
        }

        Label sub = new Label(subtext != null ? subtext : "");
        sub.getStyleClass().add("text-muted");

        card.getChildren().addAll(lbl, val, sub);
        return card;
    }

    public static Label createBadge(String text, String badgeClass) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", badgeClass);
        return badge;
    }

    public static Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-primary");
        return btn;
    }

    public static Button createSuccessButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-success");
        return btn;
    }

    public static Button createDangerButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-danger");
        return btn;
    }

    public static Button createWarningButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-warning");
        return btn;
    }

    public static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static HBox createHeaderBar(String title, String subtitle, Node rightNode) {
        HBox bar = new HBox(20);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 16, 0));

        VBox titleBox = new VBox(4);
        Label mainTitle = new Label(title);
        mainTitle.getStyleClass().add("heading-1");

        Label subTitle = new Label(subtitle);
        subTitle.getStyleClass().add("text-muted");

        titleBox.getChildren().addAll(mainTitle, subTitle);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        bar.getChildren().add(titleBox);
        if (rightNode != null) {
            bar.getChildren().add(rightNode);
        }
        return bar;
    }
}
