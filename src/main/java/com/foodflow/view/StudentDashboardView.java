package com.foodflow.view;

import com.foodflow.MainApp;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.*;
import com.foodflow.model.Menu;
import com.foodflow.model.enums.ComplaintCategory;
import com.foodflow.model.enums.MealType;
import com.foodflow.service.*;
import com.foodflow.util.DateUtil;
import com.foodflow.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

/**
 * Interactive Student Dashboard for FoodFlow Mess Management Portal.
 */
public class StudentDashboardView {

    private final MainApp mainApp;
    private final MenuService menuService;
    private final AttendanceService attendanceService;
    private final FeedbackService feedbackService;
    private final ComplaintService complaintService;
    private final BillingService billingService;
    private final NotificationService notificationService;
    private final Student currentStudent;

    public StudentDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.menuService = new MenuService();
        this.attendanceService = new AttendanceService();
        this.feedbackService = new FeedbackService();
        this.complaintService = new ComplaintService();
        this.billingService = new BillingService();
        this.notificationService = new NotificationService();
        this.currentStudent = SessionManager.getInstance().getCurrentStudent();
    }

    public Pane getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-window");

        // Top Navigation Bar
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.getStyleClass().add("sidebar");

        Label logoLbl = new Label("FoodFlow");
        logoLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #818cf8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label welcomeLbl = new Label(currentStudent != null ?
                "👋 " + currentStudent.getName() + " (" + currentStudent.getRegistrationNumber() + ")" : "Student Portal");
        welcomeLbl.getStyleClass().add("heading-3");

        Button notifBtn = new Button("🔔 Notifications");
        notifBtn.setOnAction(e -> showNotificationsDialog());

        Button logoutBtn = UIComponents.createDangerButton("Logout");
        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().logout();
            mainApp.showLoginView();
        });

        topBar.getChildren().addAll(logoLbl, spacer, welcomeLbl, notifBtn, logoutBtn);
        root.setTop(topBar);

        // Main Tab Pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab homeTab = new Tab("📊 Dashboard Overview", createOverviewPane());
        Tab menuTab = new Tab("🍽️ Weekly Menu", createMenuPane());
        Tab attendanceTab = new Tab("✅ Meal Attendance", createAttendancePane());
        Tab feedbackTab = new Tab("⭐ Feedback", createFeedbackPane());
        Tab complaintsTab = new Tab("📝 Grievances / Complaints", createComplaintsPane());
        Tab billingTab = new Tab("💳 Monthly Billing", createBillingPane());

        tabPane.getTabs().addAll(homeTab, menuTab, attendanceTab, feedbackTab, complaintsTab, billingTab);
        root.setCenter(tabPane);

        return root;
    }

    private Pane createOverviewPane() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        HBox header = UIComponents.createHeaderBar(
                "Good Morning, " + (currentStudent != null ? currentStudent.getName() : "Student") + "!",
                "Hostel: " + (currentStudent != null ? currentStudent.getHostel() : "N/A") + " | " + DateUtil.formatDate(LocalDate.now()),
                null
        );

        // KPI Summary Cards
        int totalMeals = 0;
        try {
            if (currentStudent != null) {
                totalMeals = attendanceService.getMonthlyMealCount(currentStudent.getId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            }
        } catch (Exception ignored) {}

        HBox kpiRow = new HBox(16);
        VBox kpi1 = UIComponents.createKpiCard("MONTHLY MEALS", String.valueOf(totalMeals), "Consumed in " + DateUtil.getCurrentMonthYear(), "#38bdf8");
        VBox kpi2 = UIComponents.createKpiCard("ATTENDANCE %", String.format("%.1f%%", Math.min(100.0, (totalMeals / 72.0) * 100)), "Target 75%+", "#34d399");
        VBox kpi3 = UIComponents.createKpiCard("CURRENT BILL", "₹" + (totalMeals * 30), "Est. due this month", "#fbbf24");
        VBox kpi4 = UIComponents.createKpiCard("ENROLLMENT", currentStudent != null && currentStudent.isEnrolled() ? "ACTIVE" : "INACTIVE", "Mess Plan", "#a78bfa");
        kpiRow.getChildren().addAll(kpi1, kpi2, kpi3, kpi4);

        // Middle Row: Today's Menu & Today's Attendance
        HBox midRow = new HBox(20);

        // Today's Menu Card
        Menu todayMenu = null;
        try {
            todayMenu = menuService.getTodayMenu();
        } catch (Exception ignored) {}

        VBox menuCard = new VBox(12);
        menuCard.getStyleClass().add("glass-card");
        HBox.setHgrow(menuCard, Priority.ALWAYS);

        Label menuCardTitle = new Label("🍽️ Today's Menu (" + DateUtil.formatDate(LocalDate.now()) + ")");
        menuCardTitle.getStyleClass().add("heading-3");

        VBox mealList = new VBox(8);
        mealList.getChildren().addAll(
                createMealRow("Breakfast", todayMenu != null ? todayMenu.getBreakfast() : "Poha, Boiled Eggs / Banana, Tea"),
                createMealRow("Lunch", todayMenu != null ? todayMenu.getLunch() : "South Indian Thali, Sambar, Curd"),
                createMealRow("Snacks", todayMenu != null ? todayMenu.getSnacks() : "Samosa, Masala Chai"),
                createMealRow("Dinner", todayMenu != null ? todayMenu.getDinner() : "Butter Naan, Paneer Butter Masala, Rice")
        );

        if (todayMenu != null && todayMenu.getNutrition() != null) {
            Label nutLbl = new Label("⚡ Nutrition: " + todayMenu.getNutrition().toString());
            nutLbl.getStyleClass().add("text-muted");
            mealList.getChildren().add(nutLbl);
        }
        menuCard.getChildren().addAll(menuCardTitle, mealList);

        // Today's Attendance Card
        VBox attCard = new VBox(12);
        attCard.getStyleClass().add("glass-card");
        HBox.setHgrow(attCard, Priority.ALWAYS);

        Label attTitle = new Label("✅ Today's Attendance Status");
        attTitle.getStyleClass().add("heading-3");

        VBox attList = new VBox(10);
        for (MealType meal : MealType.values()) {
            boolean marked = false;
            try {
                if (currentStudent != null) {
                    marked = attendanceService.hasStudentAttended(currentStudent.getId(), LocalDate.now(), meal);
                }
            } catch (Exception ignored) {}

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            Label mealLbl = new Label(meal.getLabel() + " (" + meal.getTiming() + ")");
            mealLbl.setStyle("-fx-font-weight: bold;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            if (marked) {
                Label badge = UIComponents.createBadge("PRESENT ✓", "badge-success");
                row.getChildren().addAll(mealLbl, sp, badge);
            } else {
                Button markBtn = UIComponents.createPrimaryButton("Mark Meal");
                markBtn.setOnAction(e -> {
                    try {
                        attendanceService.markAttendanceSynchronized(currentStudent.getId(), LocalDate.now(), meal);
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Attendance Marked",
                                "Marked " + meal.getLabel() + " attendance successfully!");
                        mainApp.showStudentDashboard();
                    } catch (Exception ex) {
                        UIComponents.showAlert(Alert.AlertType.ERROR, "Attendance Error", "Could not mark attendance", ex.getMessage());
                    }
                });
                row.getChildren().addAll(mealLbl, sp, markBtn);
            }
            attList.getChildren().add(row);
        }

        attCard.getChildren().addAll(attTitle, attList);
        midRow.getChildren().addAll(menuCard, attCard);

        content.getChildren().addAll(header, kpiRow, midRow);
        scroll.setContent(content);
        return new StackPane(scroll);
    }

    private HBox createMealRow(String mealName, String items) {
        HBox row = new HBox(8);
        Label title = new Label(mealName + ":");
        title.setStyle("-fx-font-weight: bold; -fx-min-width: 80px; -fx-text-fill: #a5b4fc;");
        Label desc = new Label(items);
        desc.setWrapText(true);
        row.getChildren().addAll(title, desc);
        return row;
    }

    private Pane createMenuPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Weekly Mess Menu Schedule");
        title.getStyleClass().add("heading-2");

        TableView<Menu> table = new TableView<>();
        TableColumn<Menu, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(DateUtil.formatDate(d.getValue().getMenuDate())));
        dateCol.setPrefWidth(120);

        TableColumn<Menu, String> bCol = new TableColumn<>("Breakfast (07:30-09:30)");
        bCol.setCellValueFactory(new PropertyValueFactory<>("breakfast"));
        bCol.setPrefWidth(220);

        TableColumn<Menu, String> lCol = new TableColumn<>("Lunch (12:00-14:00)");
        lCol.setCellValueFactory(new PropertyValueFactory<>("lunch"));
        lCol.setPrefWidth(240);

        TableColumn<Menu, String> sCol = new TableColumn<>("Snacks (16:30-18:00)");
        sCol.setCellValueFactory(new PropertyValueFactory<>("snacks"));
        sCol.setPrefWidth(180);

        TableColumn<Menu, String> dCol = new TableColumn<>("Dinner (19:30-21:30)");
        dCol.setCellValueFactory(new PropertyValueFactory<>("dinner"));
        dCol.setPrefWidth(240);

        table.getColumns().addAll(dateCol, bCol, lCol, sCol, dCol);
        try {
            table.setItems(FXCollections.observableArrayList(menuService.getAllMenus()));
        } catch (Exception ignored) {}

        box.getChildren().addAll(title, table);
        return box;
    }

    private Pane createAttendancePane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Your Meal Attendance History");
        title.getStyleClass().add("heading-2");

        TableView<MealAttendance> table = new TableView<>();
        TableColumn<MealAttendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(DateUtil.formatDate(d.getValue().getAttendanceDate())));

        TableColumn<MealAttendance, String> mealCol = new TableColumn<>("Meal Type");
        mealCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMealType().getLabel()));

        TableColumn<MealAttendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<MealAttendance, String> timeCol = new TableColumn<>("Marked Time");
        timeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(DateUtil.formatDateTime(d.getValue().getMarkedAt())));
        timeCol.setPrefWidth(200);

        table.getColumns().addAll(dateCol, mealCol, statusCol, timeCol);
        try {
            if (currentStudent != null) {
                table.setItems(FXCollections.observableArrayList(attendanceService.getStudentAttendanceHistory(currentStudent.getId())));
            }
        } catch (Exception ignored) {}

        box.getChildren().addAll(title, table);
        return box;
    }

    private Pane createFeedbackPane() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(24));

        Label title = new Label("Submit Meal Quality Feedback");
        title.getStyleClass().add("heading-2");

        VBox formCard = new VBox(12);
        formCard.getStyleClass().add("glass-card");

        ComboBox<MealType> mealBox = new ComboBox<>(FXCollections.observableArrayList(MealType.values()));
        mealBox.setValue(MealType.LUNCH);

        ComboBox<Integer> ratingBox = new ComboBox<>(FXCollections.observableArrayList(5, 4, 3, 2, 1));
        ratingBox.setValue(5);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Enter your comments regarding food taste, quality, freshness...");
        commentArea.setPrefRowCount(3);

        Button submitBtn = UIComponents.createPrimaryButton("Submit Review");
        submitBtn.setOnAction(e -> {
            try {
                feedbackService.submitFeedback(currentStudent.getId(), mealBox.getValue(), ratingBox.getValue(), commentArea.getText());
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Feedback Submitted", "Thank you for your review!");
                commentArea.clear();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Could not submit feedback", ex.getMessage());
            }
        });

        formCard.getChildren().addAll(
                new Label("Select Meal:"), mealBox,
                new Label("Rating (1-5 Stars):"), ratingBox,
                new Label("Comments:"), commentArea,
                submitBtn
        );

        box.getChildren().addAll(title, formCard);
        return box;
    }

    private Pane createComplaintsPane() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(24));

        Label title = new Label("Grievance Redressal & Complaint Tracker");
        title.getStyleClass().add("heading-2");

        VBox form = new VBox(12);
        form.getStyleClass().add("glass-card");

        ComboBox<ComplaintCategory> catBox = new ComboBox<>(FXCollections.observableArrayList(ComplaintCategory.values()));
        catBox.setValue(ComplaintCategory.FOOD_QUALITY);

        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the issue clearly (at least 10 characters)...");
        descArea.setPrefRowCount(3);

        Button submitBtn = UIComponents.createWarningButton("File Grievance");
        submitBtn.setOnAction(e -> {
            try {
                complaintService.fileComplaint(currentStudent.getId(), catBox.getValue(), descArea.getText());
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Filed", "Complaint Submitted", "Your complaint has been submitted for Warden review.");
                descArea.clear();
                mainApp.showStudentDashboard();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Failed to file complaint", ex.getMessage());
            }
        });

        form.getChildren().addAll(new Label("Category:"), catBox, new Label("Description:"), descArea, submitBtn);

        Label subTitle = new Label("Your Filed Complaints:");
        subTitle.getStyleClass().add("heading-3");

        TableView<Complaint> table = new TableView<>();
        TableColumn<Complaint, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("complaintId"));

        TableColumn<Complaint, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCategory().getLabel()));

        TableColumn<Complaint, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        TableColumn<Complaint, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus().getDisplay()));

        TableColumn<Complaint, String> remarksCol = new TableColumn<>("Admin Remarks");
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("adminRemarks"));
        remarksCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, catCol, descCol, statusCol, remarksCol);
        try {
            if (currentStudent != null) {
                table.setItems(FXCollections.observableArrayList(complaintService.getComplaintsByStudent(currentStudent.getId())));
            }
        } catch (Exception ignored) {}

        box.getChildren().addAll(title, form, subTitle, table);
        return box;
    }

    private Pane createBillingPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Monthly Invoices & Payment Summary");
        title.getStyleClass().add("heading-2");

        TableView<Bill> table = new TableView<>();
        TableColumn<Bill, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<>("billingMonth"));

        TableColumn<Bill, Integer> mealsCol = new TableColumn<>("Total Meals");
        mealsCol.setCellValueFactory(new PropertyValueFactory<>("totalMeals"));

        TableColumn<Bill, String> amtCol = new TableColumn<>("Amount (INR)");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty("₹" + d.getValue().getAmount()));

        TableColumn<Bill, String> statusCol = new TableColumn<>("Payment Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentStatus().getLabel()));

        table.getColumns().addAll(monthCol, mealsCol, amtCol, statusCol);
        try {
            if (currentStudent != null) {
                table.setItems(FXCollections.observableArrayList(billingService.getBillsByStudent(currentStudent.getId())));
            }
        } catch (Exception ignored) {}

        box.getChildren().addAll(title, table);
        return box;
    }

    private void showNotificationsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Student Notifications");
        dialog.setHeaderText("Announcements & Alerts (Vector Collection History)");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setMinWidth(400);

        try {
            Vector<Notification> notifs = notificationService.getNotificationsForStudent(currentStudent.getId());
            if (notifs.isEmpty()) {
                content.getChildren().add(new Label("No notifications at this time."));
            } else {
                for (Notification n : notifs) {
                    Label lbl = new Label("• [" + DateUtil.formatDate(n.getNotificationDate()) + "] " + n.getMessage());
                    lbl.setWrapText(true);
                    content.getChildren().add(lbl);
                }
            }
        } catch (Exception e) {
            content.getChildren().add(new Label("Error loading notifications: " + e.getMessage()));
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
