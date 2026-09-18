package com.foodflow.service;

import com.foodflow.dao.MenuJDBCDAO;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.Menu;
import com.foodflow.util.LoggerUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Service managing daily and weekly menus.
 * Demonstrates:
 * - Collections: Stack for "Undo Last Action" admin history feature
 * - Collections: ArrayList for menu lists
 * - Date manipulation
 */
public class MenuService {

    public enum ActionType {
        ADD,
        UPDATE,
        DELETE
    }

    /**
     * DTO recording an action performed on a menu for the undo stack.
     */
    public static class MenuAction {
        private final ActionType type;
        private final Menu previousState;
        private final Menu currentState;
        private final String description;

        public MenuAction(ActionType type, Menu previousState, Menu currentState, String description) {
            this.type = type;
            this.previousState = previousState;
            this.currentState = currentState;
            this.description = description;
        }

        public ActionType getType() { return type; }
        public Menu getPreviousState() { return previousState; }
        public Menu getCurrentState() { return currentState; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return String.format("[%s] %s", type, description);
        }
    }

    private final MenuJDBCDAO menuDAO;
    private final Stack<MenuAction> actionHistory;

    public MenuService() {
        this.menuDAO = new MenuJDBCDAO();
        this.actionHistory = new Stack<>();
    }

    public Menu getTodayMenu() throws DatabaseException {
        Menu menu = menuDAO.findByDate(LocalDate.now());
        if (menu == null) {
            // Return default placeholder menu
            menu = new Menu(null, LocalDate.now(),
                    "Idli, Vada, Chutney, Sambar, Coffee",
                    "Veg Meals, Paneer Curry, Dal, Rice, Curd",
                    "Veg Puff, Tea / Coffee",
                    "Phulka, Mix Veg Curry, Jeera Rice, Sweet",
                    new Menu.NutritionInfo(2250, 75.0, 280.0, 60.0));
        }
        return menu;
    }

    public Menu getMenuByDate(LocalDate date) throws DatabaseException {
        return menuDAO.findByDate(date);
    }

    public List<Menu> getWeeklyMenu(LocalDate startDate) throws DatabaseException {
        return menuDAO.findWeeklyMenu(startDate);
    }

    public List<Menu> getAllMenus() throws DatabaseException {
        return menuDAO.findAll();
    }

    public Menu saveOrUpdateMenu(Menu menu) throws DatabaseException {
        Menu existing = menuDAO.findByDate(menu.getMenuDate());
        Menu saved = menuDAO.createOrUpdateMenu(menu);

        if (existing == null) {
            actionHistory.push(new MenuAction(ActionType.ADD, null, saved, "Added menu for " + saved.getMenuDate()));
            LoggerUtil.info(MenuService.class, "Recorded ADD action on stack for date " + saved.getMenuDate());
        } else {
            actionHistory.push(new MenuAction(ActionType.UPDATE, existing, saved, "Updated menu for " + saved.getMenuDate()));
            LoggerUtil.info(MenuService.class, "Recorded UPDATE action on stack for date " + saved.getMenuDate());
        }

        return saved;
    }

    public void deleteMenu(Long menuId) throws DatabaseException {
        List<Menu> all = menuDAO.findAll();
        Menu toDelete = null;
        for (Menu m : all) {
            if (m.getMenuId().equals(menuId)) {
                toDelete = m;
                break;
            }
        }

        if (toDelete != null) {
            actionHistory.push(new MenuAction(ActionType.DELETE, toDelete, null, "Deleted menu for " + toDelete.getMenuDate()));
            LoggerUtil.info(MenuService.class, "Recorded DELETE action on stack for date " + toDelete.getMenuDate());
        }

        menuDAO.deleteMenu(menuId);
    }

    /**
     * Reverts the most recent menu administrative action using the Stack.
     * Demonstrates Stack LIFO (Last-In-First-Out) operations.
     */
    public String undoLastAction() throws FoodFlowException {
        if (actionHistory.isEmpty()) {
            throw new FoodFlowException("No actions available to undo in history stack.", "ERR_STACK_EMPTY");
        }

        MenuAction lastAction = actionHistory.pop();
        LoggerUtil.info(MenuService.class, "Popped action from stack: " + lastAction);

        switch (lastAction.getType()) {
            case ADD -> {
                // To undo an ADD, delete the newly added menu
                if (lastAction.getCurrentState() != null && lastAction.getCurrentState().getMenuId() != null) {
                    menuDAO.deleteMenu(lastAction.getCurrentState().getMenuId());
                }
                return "Undone: Reverted creation of menu for " + lastAction.getCurrentState().getMenuDate();
            }
            case UPDATE -> {
                // To undo an UPDATE, restore the previous state
                if (lastAction.getPreviousState() != null) {
                    menuDAO.createOrUpdateMenu(lastAction.getPreviousState());
                }
                return "Undone: Restored previous menu state for " + lastAction.getPreviousState().getMenuDate();
            }
            case DELETE -> {
                // To undo a DELETE, re-insert the deleted menu
                if (lastAction.getPreviousState() != null) {
                    menuDAO.createOrUpdateMenu(lastAction.getPreviousState());
                }
                return "Undone: Restored deleted menu for " + lastAction.getPreviousState().getMenuDate();
            }
        }
        return "Undone action: " + lastAction.getDescription();
    }

    public Stack<MenuAction> getActionHistory() {
        return actionHistory;
    }

    public boolean canUndo() {
        return !actionHistory.isEmpty();
    }
}
