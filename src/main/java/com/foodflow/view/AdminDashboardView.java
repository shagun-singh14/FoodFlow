package com.foodflow.view;

import com.foodflow.MainApp;
import com.foodflow.entity.StudentEntity;
import com.foodflow.entity.UserEntity;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.*;
import com.foodflow.model.Menu;
import com.foodflow.model.enums.*;
import com.foodflow.report.*;
import com.foodflow.service.*;
import com.foodflow.thread.AttendanceSimulationManager;
import com.foodflow.util.DateUtil;
import com.foodflow.util.ReflectionInspector;
import com.foodflow.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Modern Comprehensive Admin Dashboard for Mess Wardens and Administrators.
 */
public class AdminDashboardView {

    private final MainApp mainApp;
    private final StudentService studentService;
    private final MenuService menuService;
    private final AttendanceService attendanceService;
    private final FeedbackService feedbackService;
    private final ComplaintService complaintService;
    private final BillingService billingService;
    private final AnalyticsService analyticsService;
    private final ReportGenerator reportGenerator;
    private final Admin currentAdmin;

    public AdminDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.studentService = new StudentService();
        this.menuService = new MenuService();
        this.attendanceService = new AttendanceService();
        this.feedbackService = new FeedbackService();
        this.complaintService = new ComplaintService();
        this.billingService = new BillingService();
        this.analyticsService = new AnalyticsService();
        this.reportGenerator = new ReportGenerator();
        this.currentAdmin = SessionManager.getInstance().getCurrentAdmin();
    }

    public Pane getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-window");

        // Top Navigation Header
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.getStyleClass().add("sidebar");

        Label logoLbl = new Label("FoodFlow Admin Console");
        logoLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #818cf8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminInfoLbl = new Label(currentAdmin != null ? "🛡️ " + currentAdmin.getName() : "Administrator");
        adminInfoLbl.getStyleClass().add("heading-3");

        Button logoutBtn = UIComponents.createDangerButton("Logout");
        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().logout();
            mainApp.showLoginView();
        });

        topBar.getChildren().addAll(logoLbl, spacer, adminInfoLbl, logoutBtn);
        root.setTop(topBar);

        // Tabbed Admin Navigation
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab kpiTab = new Tab("📊 Operational Overview", createOverviewPane());
        Tab studentTab = new Tab("👥 Students (Overloaded Search)", createStudentPane());
        Tab menuTab = new Tab("🍽️ Menu (Stack Undo)", createMenuPane());
        Tab simTab = new Tab("⚡ Concurrency & Attendance", createSimulationPane());
        Tab feedbackTab = new Tab("⭐ Feedback Analytics", createFeedbackPane());
        Tab complaintTab = new Tab("📝 Grievances", createComplaintPane());
        Tab billingTab = new Tab("💳 Billing & Payments", createBillingPane());
        Tab reportTab = new Tab("📄 I/O Reports Export", createReportsPane());
        Tab arrayTab = new Tab("📐 1D/2D/Jagged Arrays", createArraysPane());
        Tab reflectTab = new Tab("🔍 Reflection Explorer", createReflectionPane());

        tabPane.getTabs().addAll(kpiTab, studentTab, menuTab, simTab, feedbackTab, complaintTab, billingTab, reportTab, arrayTab, reflectTab);
        root.setCenter(tabPane);

        return root;
    }

    private Pane createOverviewPane() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        HBox header = UIComponents.createHeaderBar(
                "Mess Administration Dashboard",
                "Centralized Monitoring & Operational Analytics • " + DateUtil.formatDate(LocalDate.now()),
                null
        );

        // Fetch Live Metric Figures
        int totalStudents = 0;
        int todayAttendance = 0;
        double avgRating = 0.0;
        int openComplaints = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        try {
            totalStudents = studentService.getAllStudents().size();
            todayAttendance = attendanceService.getAttendanceByDate(LocalDate.now()).size();
            avgRating = feedbackService.calculateOverallAverageRating();
            openComplaints = complaintService.getOpenComplaintsCount();
            totalRevenue = billingService.calculateTotalRevenue();
        } catch (Exception ignored) {}

        // KPI Metric Cards
        HBox kpiRow = new HBox(16);
        VBox c1 = UIComponents.createKpiCard("TOTAL STUDENTS", String.valueOf(totalStudents), "Active in Mess", "#38bdf8");
        VBox c2 = UIComponents.createKpiCard("TODAY ATTENDANCE", String.valueOf(todayAttendance), "Meals served today", "#34d399");
        VBox c3 = UIComponents.createKpiCard("AVG FOOD RATING", String.format("%.2f ★", avgRating), "Out of 5.00", "#fbbf24");
        VBox c4 = UIComponents.createKpiCard("OPEN COMPLAINTS", String.valueOf(openComplaints), "Requires Action", "#f87171");
        VBox c5 = UIComponents.createKpiCard("TOTAL REVENUE", "₹" + totalRevenue.doubleValue(), "Collected Dues", "#a78bfa");
        kpiRow.getChildren().addAll(c1, c2, c3, c4, c5);

        // Meal Session Distribution (1-D Array calculation)
        VBox statsCard = new VBox(12);
        statsCard.getStyleClass().add("glass-card");

        Label statsTitle = new Label("🍽️ Today's Meal Consumption by Session (1-D Array Stats)");
        statsTitle.getStyleClass().add("heading-3");

        int[] dailyCounts = new int[4];
        try {
            dailyCounts = analyticsService.calculateDailyMealStatistics(LocalDate.now());
        } catch (Exception ignored) {}

        HBox mealCountsRow = new HBox(20);
        mealCountsRow.getChildren().addAll(
                UIComponents.createKpiCard("BREAKFAST", String.valueOf(dailyCounts[0]), "07:30 - 09:30", "#38bdf8"),
                UIComponents.createKpiCard("LUNCH", String.valueOf(dailyCounts[1]), "12:00 - 14:00", "#34d399"),
                UIComponents.createKpiCard("SNACKS", String.valueOf(dailyCounts[2]), "16:30 - 18:00", "#fbbf24"),
                UIComponents.createKpiCard("DINNER", String.valueOf(dailyCounts[3]), "19:30 - 21:30", "#818cf8")
        );
        statsCard.getChildren().addAll(statsTitle, mealCountsRow);

        content.getChildren().addAll(header, kpiRow, statsCard);
        scroll.setContent(content);
        return new StackPane(scroll);
    }

    private Pane createStudentPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Student Enrollment & Management (Method Overloading Search)");
        title.getStyleClass().add("heading-2");

        // Overloaded Search Form
        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        TextField nameSearchField = new TextField();
        nameSearchField.setPromptText("Search by Name...");

        TextField idSearchField = new TextField();
        idSearchField.setPromptText("Search by ID (int)...");
        idSearchField.setMaxWidth(130);

        TextField courseSearchField = new TextField();
        courseSearchField.setPromptText("Filter by Course...");

        Button searchNameBtn = new Button("Search (Name)");
        Button searchIdBtn = new Button("Search (ID)");
        Button searchComboBtn = new Button("Search (Name + Course)");
        Button resetBtn = new Button("Reset All");

        searchRow.getChildren().addAll(nameSearchField, searchNameBtn, idSearchField, searchIdBtn, courseSearchField, searchComboBtn, resetBtn);

        TableView<Student> table = new TableView<>();
        TableColumn<Student, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> regCol = new TableColumn<>("Reg Number");
        regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));

        TableColumn<Student, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Student, String> hostelCol = new TableColumn<>("Hostel");
        hostelCol.setCellValueFactory(new PropertyValueFactory<>("hostel"));

        TableColumn<Student, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Student, String> enrollCol = new TableColumn<>("Enrolled");
        enrollCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().isEnrolled() ? "YES" : "NO"));

        table.getColumns().addAll(idCol, nameCol, regCol, courseCol, yearCol, hostelCol, roomCol, enrollCol);

        Runnable refreshTable = () -> {
            try {
                table.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
            } catch (Exception ignored) {}
        };
        refreshTable.run();

        // Search Handlers demonstrating Method Overloading
        searchNameBtn.setOnAction(e -> {
            try {
                // Call searchStudent(String name)
                table.setItems(FXCollections.observableArrayList(studentService.searchStudent(nameSearchField.getText())));
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Search failed", ex.getMessage());
            }
        });

        searchIdBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idSearchField.getText().trim());
                // Call searchStudent(int id)
                table.setItems(FXCollections.observableArrayList(studentService.searchStudent(id)));
            } catch (NumberFormatException nfe) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Invalid ID", "Numeric input required", "Please enter a valid numeric ID.");
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Search failed", ex.getMessage());
            }
        });

        searchComboBtn.setOnAction(e -> {
            try {
                // Call searchStudent(String name, String course)
                table.setItems(FXCollections.observableArrayList(
                        studentService.searchStudent(nameSearchField.getText(), courseSearchField.getText())));
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Search failed", ex.getMessage());
            }
        });

        resetBtn.setOnAction(e -> {
            nameSearchField.clear();
            idSearchField.clear();
            courseSearchField.clear();
            refreshTable.run();
        });

        // Toggle Enrollment Actions
        HBox actionRow = new HBox(10);
        Button toggleEnrollBtn = UIComponents.createWarningButton("Toggle Selected Enrollment");
        toggleEnrollBtn.setOnAction(e -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Select Student", "No selection", "Please select a student row first.");
                return;
            }
            try {
                if (selected.isEnrolled()) {
                    studentService.deactivateStudent(selected.getId());
                } else {
                    studentService.reactivateStudent(selected.getId());
                }
                refreshTable.run();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Enrollment update failed", ex.getMessage());
            }
        });
        actionRow.getChildren().add(toggleEnrollBtn);

        box.getChildren().addAll(title, searchRow, table, actionRow);
        return box;
    }

    private Pane createMenuPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Menu Management & Stack Undo Operations");
        title.getStyleClass().add("heading-2");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button undoBtn = UIComponents.createWarningButton("↩ Undo Last Menu Action (Stack)");
        topRow.getChildren().addAll(title, sp, undoBtn);

        // Add / Edit Form Card
        VBox formCard = new VBox(10);
        formCard.getStyleClass().add("glass-card");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField bField = new TextField(); bField.setPromptText("Breakfast items");
        TextField lField = new TextField(); lField.setPromptText("Lunch items");
        TextField sField = new TextField(); sField.setPromptText("Snacks items");
        TextField dField = new TextField(); dField.setPromptText("Dinner items");

        Button saveMenuBtn = UIComponents.createSuccessButton("Save / Update Menu");

        TableView<Menu> table = new TableView<>();
        TableColumn<Menu, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(DateUtil.formatDate(d.getValue().getMenuDate())));
        TableColumn<Menu, String> bCol = new TableColumn<>("Breakfast"); bCol.setCellValueFactory(new PropertyValueFactory<>("breakfast"));
        TableColumn<Menu, String> lCol = new TableColumn<>("Lunch"); lCol.setCellValueFactory(new PropertyValueFactory<>("lunch"));
        TableColumn<Menu, String> sCol = new TableColumn<>("Snacks"); sCol.setCellValueFactory(new PropertyValueFactory<>("snacks"));
        TableColumn<Menu, String> dCol = new TableColumn<>("Dinner"); dCol.setCellValueFactory(new PropertyValueFactory<>("dinner"));
        table.getColumns().addAll(dateCol, bCol, lCol, sCol, dCol);

        Runnable refreshTable = () -> {
            try {
                table.setItems(FXCollections.observableArrayList(menuService.getAllMenus()));
            } catch (Exception ignored) {}
        };
        refreshTable.run();

        saveMenuBtn.setOnAction(e -> {
            try {
                Menu menu = new Menu(null, datePicker.getValue(), bField.getText(), lField.getText(), sField.getText(), dField.getText(),
                        new Menu.NutritionInfo(2250, 75.0, 280.0, 60.0));
                menuService.saveOrUpdateMenu(menu);
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Menu Saved", "Menu saved and recorded onto Undo Stack.");
                refreshTable.run();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Failed to save menu", ex.getMessage());
            }
        });

        undoBtn.setOnAction(e -> {
            try {
                String message = menuService.undoLastAction();
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Undo Stack", "Action Reverted", message);
                refreshTable.run();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Undo Stack", "Cannot Undo", ex.getMessage());
            }
        });

        formCard.getChildren().addAll(new Label("Date:"), datePicker,
                new Label("Breakfast:"), bField, new Label("Lunch:"), lField,
                new Label("Snacks:"), sField, new Label("Dinner:"), dField, saveMenuBtn);

        box.getChildren().addAll(topRow, formCard, table);
        return box;
    }

    private Pane createSimulationPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Multithreading & Synchronized Meal Attendance Simulator");
        title.getStyleClass().add("heading-2");

        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");

        Label desc = new Label("Simulate multiple student threads marking attendance concurrently to verify thread safety and race-condition prevention.");
        desc.getStyleClass().add("text-muted");

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        ComboBox<MealType> mealBox = new ComboBox<>(FXCollections.observableArrayList(MealType.values()));
        mealBox.setValue(MealType.BREAKFAST);

        ComboBox<Integer> threadsBox = new ComboBox<>(FXCollections.observableArrayList(2, 4, 8, 12, 16));
        threadsBox.setValue(4);

        Button runSimBtn = UIComponents.createPrimaryButton("🚀 Launch Concurrency Test");

        controls.getChildren().addAll(new Label("Meal:"), mealBox, new Label("Worker Threads:"), threadsBox, runSimBtn);

        TextArea logArea = new TextArea();
        logArea.setPrefRowCount(10);
        logArea.setEditable(false);

        AttendanceSimulationManager simManager = new AttendanceSimulationManager(attendanceService);

        runSimBtn.setOnAction(e -> {
            try {
                List<Student> students = studentService.getAllStudents();
                AttendanceSimulationManager.SimulationResult res =
                        simManager.runConcurrentSimulation(students, LocalDate.now(), mealBox.getValue(), threadsBox.getValue());

                StringBuilder sb = new StringBuilder();
                sb.append("=== CONCURRENCY SIMULATION RESULTS ===\n");
                sb.append("Total Student Threads Attempted: ").append(res.getTotalAttempted()).append("\n");
                sb.append("Successful Transactions         : ").append(res.getSuccessfulCount()).append("\n");
                sb.append("Duplicate / Prevented Races     : ").append(res.getDuplicateCount()).append("\n");
                sb.append("Execution Time (ms)             : ").append(res.getExecutionTimeMillis()).append(" ms\n\n");
                sb.append("--- Detailed Thread Logs ---\n");
                for (String l : res.getLogs()) {
                    sb.append(l).append("\n");
                }
                logArea.setText(sb.toString());
            } catch (Exception ex) {
                logArea.setText("Simulation Error: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(desc, controls, logArea);
        box.getChildren().addAll(title, card);
        return box;
    }

    private Pane createFeedbackPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Meal Feedback & Quality Ratings");
        title.getStyleClass().add("heading-2");

        TableView<Feedback> table = new TableView<>();
        TableColumn<Feedback, Long> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        TableColumn<Feedback, String> stuCol = new TableColumn<>("Student"); stuCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        TableColumn<Feedback, String> mealCol = new TableColumn<>("Meal"); mealCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMealType().getLabel()));
        TableColumn<Feedback, Integer> ratCol = new TableColumn<>("Rating"); ratCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        TableColumn<Feedback, String> commCol = new TableColumn<>("Comments"); commCol.setCellValueFactory(new PropertyValueFactory<>("comments")); commCol.setPrefWidth(250);
        TableColumn<Feedback, String> dateCol = new TableColumn<>("Date"); dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(DateUtil.formatDate(d.getValue().getFeedbackDate())));

        table.getColumns().addAll(idCol, stuCol, mealCol, ratCol, commCol, dateCol);
        try {
            table.setItems(FXCollections.observableArrayList(feedbackService.getAllFeedbacks()));
        } catch (Exception ignored) {}

        box.getChildren().addAll(title, table);
        return box;
    }

    private Pane createComplaintPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Grievance Redressal & Resolution");
        title.getStyleClass().add("heading-2");

        TableView<Complaint> table = new TableView<>();
        TableColumn<Complaint, Long> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        TableColumn<Complaint, String> stuCol = new TableColumn<>("Student"); stuCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        TableColumn<Complaint, String> catCol = new TableColumn<>("Category"); catCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCategory().getLabel()));
        TableColumn<Complaint, String> descCol = new TableColumn<>("Description"); descCol.setCellValueFactory(new PropertyValueFactory<>("description")); descCol.setPrefWidth(250);
        TableColumn<Complaint, String> statCol = new TableColumn<>("Status"); statCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus().getDisplay()));
        TableColumn<Complaint, String> remCol = new TableColumn<>("Remarks"); remCol.setCellValueFactory(new PropertyValueFactory<>("adminRemarks")); remCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, stuCol, catCol, descCol, statCol, remCol);

        Runnable refresh = () -> {
            try {
                table.setItems(FXCollections.observableArrayList(complaintService.getAllComplaints()));
            } catch (Exception ignored) {}
        };
        refresh.run();

        // Update Action Controls
        HBox actRow = new HBox(12);
        actRow.setAlignment(Pos.CENTER_LEFT);
        ComboBox<ComplaintStatus> statusBox = new ComboBox<>(FXCollections.observableArrayList(ComplaintStatus.values()));
        statusBox.setValue(ComplaintStatus.RESOLVED);
        TextField remarksField = new TextField();
        remarksField.setPromptText("Enter resolution remarks...");
        remarksField.setPrefWidth(300);

        Button updateBtn = UIComponents.createSuccessButton("Update Selected Complaint");
        updateBtn.setOnAction(e -> {
            Complaint selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Selection Required", "No complaint selected", "Please select a complaint row.");
                return;
            }
            try {
                complaintService.updateComplaintStatus(selected.getComplaintId(), statusBox.getValue(), remarksField.getText());
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Updated", "Status Changed", "Complaint #" + selected.getComplaintId() + " updated.");
                remarksField.clear();
                refresh.run();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update complaint", ex.getMessage());
            }
        });

        actRow.getChildren().addAll(new Label("Status:"), statusBox, new Label("Remarks:"), remarksField, updateBtn);
        box.getChildren().addAll(title, table, actRow);
        return box;
    }

    private Pane createBillingPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Student Monthly Billing & Dues");
        title.getStyleClass().add("heading-2");

        TableView<Bill> table = new TableView<>();
        TableColumn<Bill, Long> idCol = new TableColumn<>("Invoice #"); idCol.setCellValueFactory(new PropertyValueFactory<>("billId"));
        TableColumn<Bill, String> stuCol = new TableColumn<>("Student"); stuCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        TableColumn<Bill, String> regCol = new TableColumn<>("Reg No"); regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        TableColumn<Bill, String> monthCol = new TableColumn<>("Month"); monthCol.setCellValueFactory(new PropertyValueFactory<>("billingMonth"));
        TableColumn<Bill, Integer> mealsCol = new TableColumn<>("Total Meals"); mealsCol.setCellValueFactory(new PropertyValueFactory<>("totalMeals"));
        TableColumn<Bill, String> amtCol = new TableColumn<>("Amount"); amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty("₹" + d.getValue().getAmount()));
        TableColumn<Bill, String> statCol = new TableColumn<>("Status"); statCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentStatus().getLabel()));

        table.getColumns().addAll(idCol, stuCol, regCol, monthCol, mealsCol, amtCol, statCol);

        Runnable refresh = () -> {
            try {
                table.setItems(FXCollections.observableArrayList(billingService.getAllBills()));
            } catch (Exception ignored) {}
        };
        refresh.run();

        HBox actRow = new HBox(12);
        Button payBtn = UIComponents.createSuccessButton("Mark Selected Invoice as PAID");
        payBtn.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    billingService.markBillPaid(selected.getBillId());
                    refresh.run();
                } catch (Exception ex) {
                    UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Payment record failed", ex.getMessage());
                }
            }
        });
        actRow.getChildren().add(payBtn);

        box.getChildren().addAll(title, table, actRow);
        return box;
    }

    private Pane createReportsPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Java I/O Reports Export Subsystem");
        title.getStyleClass().add("heading-2");

        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");

        Label desc = new Label("Export analytical mess records to formatted .txt (Character Streams: BufferedWriter), .csv, and binary backup .dat (Byte Streams: FileOutputStream).");
        desc.getStyleClass().add("text-muted");

        HBox btnRow = new HBox(12);
        Button exportTxtBtn = UIComponents.createPrimaryButton("📄 Export Mess Analytics (.txt)");
        Button exportCsvBtn = UIComponents.createSuccessButton("📊 Export All Menus (.csv)");
        Button exportBinBtn = UIComponents.createWarningButton("💾 Export Binary Backup (.dat)");

        btnRow.getChildren().addAll(exportTxtBtn, exportCsvBtn, exportBinBtn);

        TextArea previewArea = new TextArea();
        previewArea.setPrefRowCount(12);
        previewArea.setEditable(false);

        exportTxtBtn.setOnAction(e -> {
            try {
                int totalStudents = studentService.getAllStudents().size();
                int todayMeals = attendanceService.getAttendanceByDate(LocalDate.now()).size();
                double avgRating = feedbackService.calculateOverallAverageRating();
                int openComplaints = complaintService.getOpenComplaintsCount();
                BigDecimal revenue = billingService.calculateTotalRevenue();

                MessAnalyticsReport rep = new MessAnalyticsReport(
                        LocalDate.now(), totalStudents, todayMeals, avgRating, openComplaints, revenue,
                        Map.of("Breakfast", 2, "Lunch", 3, "Snacks", 1, "Dinner", 2),
                        currentAdmin != null ? currentAdmin.getName() : "Admin"
                );

                ReportGenerator.ReportSummary summary = reportGenerator.exportToTextFile(rep, "Mess_Analytics_Report");
                previewArea.setText(rep.toFormattedText() + "\n[STATUS: " + summary.getMessage() + "]");
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "I/O Success", "Text Report Generated", summary.getMessage());
            } catch (Exception ex) {
                previewArea.setText("Error exporting report: " + ex.getMessage());
            }
        });

        exportCsvBtn.setOnAction(e -> {
            try {
                List<Menu> menus = menuService.getAllMenus();
                ReportGenerator.ReportSummary summary = reportGenerator.exportToCsvFile(menus, "FoodFlow_Menus");
                previewArea.setText("CSV File generated with " + menus.size() + " menu rows.\nLocation: " + summary.getMessage());
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "I/O Success", "CSV Exported", summary.getMessage());
            } catch (Exception ex) {
                previewArea.setText("Error exporting CSV: " + ex.getMessage());
            }
        });

        exportBinBtn.setOnAction(e -> {
            try {
                byte[] sampleBackup = "FOODFLOW_BINARY_BACKUP_TOKEN_2026".getBytes();
                ReportGenerator.ReportSummary summary = reportGenerator.exportToBinaryBackup(sampleBackup, "FoodFlow_Backup");
                previewArea.setText("Byte-oriented Stream export complete: " + summary.getMessage());
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "I/O Success", "Binary Backup Generated", summary.getMessage());
            } catch (Exception ex) {
                previewArea.setText("Error exporting binary backup: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(desc, btnRow, previewArea);
        box.getChildren().addAll(title, card);
        return box;
    }

    private Pane createArraysPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Array Analytics Engine (1-D, 2-D & Jagged Arrays)");
        title.getStyleClass().add("heading-2");

        VBox card = new VBox(14);
        card.getStyleClass().add("glass-card");

        TextArea arrayOutput = new TextArea();
        arrayOutput.setPrefRowCount(14);
        arrayOutput.setEditable(false);

        Button runArrayAnalysisBtn = UIComponents.createPrimaryButton("Run Array Analysis & Matrix Computation");
        runArrayAnalysisBtn.setOnAction(e -> {
            try {
                StringBuilder sb = new StringBuilder();

                // 1-D Array
                int[] daily1D = analyticsService.calculateDailyMealStatistics(LocalDate.now());
                sb.append("======================================================================\n");
                sb.append(" 1-D ARRAY REQUIREMENT: Daily Meal Session Statistics\n");
                sb.append("======================================================================\n");
                sb.append(String.format("int[] dailyCounts = { Breakfast=%d, Lunch=%d, Snacks=%d, Dinner=%d }\n\n",
                        daily1D[0], daily1D[1], daily1D[2], daily1D[3]));

                // 2-D Array
                List<Student> students = studentService.getAllStudents();
                int[][] matrix2D = analyticsService.generateStudentMealMatrix(students, LocalDate.now());
                sb.append("======================================================================\n");
                sb.append(" 2-D ARRAY REQUIREMENT: Student × Meal Type Attendance Matrix Grid\n");
                sb.append(" Dimensions: ").append(matrix2D.length).append(" Students × 4 Meal Columns [B, L, S, D]\n");
                sb.append("======================================================================\n");
                for (int i = 0; i < matrix2D.length; i++) {
                    Student s = students.get(i);
                    sb.append(String.format("  Row %2d [%-10s - %-15s]: [B=%d, L=%d, S=%d, D=%d]\n",
                            i, s.getRegistrationNumber(), s.getName(),
                            matrix2D[i][0], matrix2D[i][1], matrix2D[i][2], matrix2D[i][3]));
                }
                sb.append("\n");

                // Jagged Array
                int[][] jagged = analyticsService.generateMonthlyJaggedAttendanceTrend(3L, 2026);
                sb.append("======================================================================\n");
                sb.append(" JAGGED ARRAY REQUIREMENT: Multi-Month Variable Days Trends (Unequal Rows)\n");
                sb.append("======================================================================\n");
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                for (int m = 0; m < jagged.length; m++) {
                    sb.append(String.format("  Month %-3s (Length: %2d days): ", months[m], jagged[m].length));
                    int recordedInMonth = 0;
                    for (int d : jagged[m]) recordedInMonth += d;
                    sb.append(recordedInMonth).append(" meals logged\n");
                }
                sb.append(" Total Computed via Jagged Array: ").append(analyticsService.sumJaggedArrayMeals(jagged)).append(" meals\n");

                arrayOutput.setText(sb.toString());
            } catch (Exception ex) {
                arrayOutput.setText("Array computation error: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(runArrayAnalysisBtn, arrayOutput);
        box.getChildren().addAll(title, card);
        return box;
    }

    private Pane createReflectionPane() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label title = new Label("Java Reflection & Class Inspector (System Metadata Tool)");
        title.getStyleClass().add("heading-2");

        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");

        HBox selectRow = new HBox(12);
        selectRow.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> classSelect = new ComboBox<>();
        classSelect.getItems().addAll(
                "com.foodflow.model.Student",
                "com.foodflow.model.Admin",
                "com.foodflow.model.User",
                "com.foodflow.model.Menu",
                "com.foodflow.entity.StudentEntity",
                "com.foodflow.entity.UserEntity",
                "com.foodflow.entity.MenuEntity"
        );
        classSelect.setValue("com.foodflow.model.Student");
        classSelect.setPrefWidth(300);

        Button inspectBtn = UIComponents.createPrimaryButton("Inspect Class (Reflection)");
        selectRow.getChildren().addAll(new Label("Select Target Class:"), classSelect, inspectBtn);

        TextArea metadataOutput = new TextArea();
        metadataOutput.setPrefRowCount(14);
        metadataOutput.setEditable(false);

        inspectBtn.setOnAction(e -> {
            try {
                ReflectionInspector.ClassMetadataReport report = ReflectionInspector.inspectByName(classSelect.getValue());
                metadataOutput.setText(report.getFormattedSummary());
            } catch (Exception ex) {
                metadataOutput.setText("Reflection Inspection Error: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(selectRow, metadataOutput);
        box.getChildren().addAll(title, card);
        return box;
    }
}
