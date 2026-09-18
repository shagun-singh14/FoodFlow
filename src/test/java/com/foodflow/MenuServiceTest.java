package com.foodflow;

import com.foodflow.model.Menu;
import com.foodflow.service.MenuService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuServiceTest {

    private static MenuService menuService;

    @BeforeAll
    public static void setup() {
        menuService = new MenuService();
    }

    @Test
    public void testGetTodayMenu() throws Exception {
        Menu menu = menuService.getTodayMenu();
        assertNotNull(menu);
        assertNotNull(menu.getBreakfast());
    }

    @Test
    public void testMenuCrudAndStackUndo() throws Exception {
        LocalDate date = LocalDate.now().plusDays(30);
        Menu menu = new Menu(null, date, "Dosa", "Rice Thali", "Tea", "Roti Curry",
                new Menu.NutritionInfo(2100, 70.0, 270.0, 50.0));

        // 1. Save Menu (Pushes to Stack)
        Menu saved = menuService.saveOrUpdateMenu(menu);
        assertNotNull(saved.getMenuId());
        assertTrue(menuService.canUndo());

        // 2. Undo Last Action (Pops from Stack)
        String undoMsg = menuService.undoLastAction();
        assertNotNull(undoMsg);
        assertTrue(undoMsg.contains("Undone"));
    }
}
