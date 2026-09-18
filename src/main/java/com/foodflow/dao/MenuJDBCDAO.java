package com.foodflow.dao;

import com.foodflow.config.DatabaseManager;
import com.foodflow.exception.DatabaseException;
import com.foodflow.model.Menu;
import com.foodflow.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Direct JDBC DAO for Menu management and scheduling.
 * Demonstrates:
 * - PreparedStatement query execution
 * - Date SQL conversions
 * - Result set processing with nested inner class instantiation
 */
public class MenuJDBCDAO {

    private final DatabaseManager dbManager;

    public MenuJDBCDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Menu createOrUpdateMenu(Menu menu) throws DatabaseException {
        String checkSql = "SELECT menu_id FROM menus WHERE menu_date = ?";
        String insertSql = """
                INSERT INTO menus (menu_date, breakfast, lunch, snacks, dinner, calories, protein_grams, carbs_grams, fat_grams)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        String updateSql = """
                UPDATE menus SET breakfast = ?, lunch = ?, snacks = ?, dinner = ?,
                                 calories = ?, protein_grams = ?, carbs_grams = ?, fat_grams = ?
                WHERE menu_date = ?
                """;

        try (Connection conn = dbManager.getConnection()) {
            Long existingId = null;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setDate(1, Date.valueOf(menu.getMenuDate()));
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getLong("menu_id");
                    }
                }
            }

            if (existingId == null) {
                // Insert
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setDate(1, Date.valueOf(menu.getMenuDate()));
                    insertStmt.setString(2, menu.getBreakfast());
                    insertStmt.setString(3, menu.getLunch());
                    insertStmt.setString(4, menu.getSnacks());
                    insertStmt.setString(5, menu.getDinner());
                    insertStmt.setInt(6, menu.getNutrition().getCalories());
                    insertStmt.setDouble(7, menu.getNutrition().getProteinGrams());
                    insertStmt.setDouble(8, menu.getNutrition().getCarbsGrams());
                    insertStmt.setDouble(9, menu.getNutrition().getFatGrams());
                    insertStmt.executeUpdate();

                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            menu.setMenuId(generatedKeys.getLong(1));
                        }
                    }
                }
            } else {
                // Update
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, menu.getBreakfast());
                    updateStmt.setString(2, menu.getLunch());
                    updateStmt.setString(3, menu.getSnacks());
                    updateStmt.setString(4, menu.getDinner());
                    updateStmt.setInt(5, menu.getNutrition().getCalories());
                    updateStmt.setDouble(6, menu.getNutrition().getProteinGrams());
                    updateStmt.setDouble(7, menu.getNutrition().getCarbsGrams());
                    updateStmt.setDouble(8, menu.getNutrition().getFatGrams());
                    updateStmt.setDate(9, Date.valueOf(menu.getMenuDate()));
                    updateStmt.executeUpdate();
                    menu.setMenuId(existingId);
                }
            }
            return menu;
        } catch (SQLException e) {
            throw new DatabaseException("Error saving menu: " + e.getMessage(), e);
        }
    }

    public Menu findByDate(LocalDate date) throws DatabaseException {
        String sql = "SELECT * FROM menus WHERE menu_date = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMenu(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding menu for date " + date + ": " + e.getMessage(), e);
        }
    }

    public List<Menu> findWeeklyMenu(LocalDate startDate) throws DatabaseException {
        String sql = "SELECT * FROM menus WHERE menu_date >= ? AND menu_date <= ? ORDER BY menu_date ASC";
        List<Menu> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(startDate.plusDays(6)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMenu(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching weekly menu: " + e.getMessage(), e);
        }
    }

    public List<Menu> findAll() throws DatabaseException {
        String sql = "SELECT * FROM menus ORDER BY menu_date DESC";
        List<Menu> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToMenu(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error listing all menus: " + e.getMessage(), e);
        }
    }

    public void deleteMenu(Long menuId) throws DatabaseException {
        String sql = "DELETE FROM menus WHERE menu_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, menuId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting menu: " + e.getMessage(), e);
        }
    }

    private Menu mapRowToMenu(ResultSet rs) throws SQLException {
        Menu.NutritionInfo nutrition = new Menu.NutritionInfo(
                rs.getInt("calories"),
                rs.getDouble("protein_grams"),
                rs.getDouble("carbs_grams"),
                rs.getDouble("fat_grams")
        );

        return new Menu(
                rs.getLong("menu_id"),
                rs.getDate("menu_date").toLocalDate(),
                rs.getString("breakfast"),
                rs.getString("lunch"),
                rs.getString("snacks"),
                rs.getString("dinner"),
                nutrition
        );
    }
}
