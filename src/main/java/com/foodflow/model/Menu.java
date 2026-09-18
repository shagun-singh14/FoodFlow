package com.foodflow.model;

import com.foodflow.model.enums.MealType;
import com.foodflow.model.interfaces.Exportable;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model class representing daily mess menu schedules.
 * Demonstrates:
 * - Nested / Inner Class (Static Nested Class: NutritionInfo)
 * - Enum mapping with MealType
 * - Exportable interface for I/O serialization
 * - Encapsulation & Defensive Copying
 */
public class Menu implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private Long menuId;
    private LocalDate menuDate;
    private String breakfast;
    private String lunch;
    private String snacks;
    private String dinner;
    private NutritionInfo nutrition;

    /**
     * Nested Inner Class: NutritionInfo
     * Encapsulates macronutrient and caloric values associated with a daily menu.
     * Demonstrates Static Nested Classes in Java.
     */
    public static class NutritionInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private int calories;
        private double proteinGrams;
        private double carbsGrams;
        private double fatGrams;

        public NutritionInfo() {
            this(2200, 75.0, 280.0, 60.0);
        }

        public NutritionInfo(int calories, double proteinGrams, double carbsGrams, double fatGrams) {
            this.calories = calories;
            this.proteinGrams = proteinGrams;
            this.carbsGrams = carbsGrams;
            this.fatGrams = fatGrams;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public double getProteinGrams() {
            return proteinGrams;
        }

        public void setProteinGrams(double proteinGrams) {
            this.proteinGrams = proteinGrams;
        }

        public double getCarbsGrams() {
            return carbsGrams;
        }

        public void setCarbsGrams(double carbsGrams) {
            this.carbsGrams = carbsGrams;
        }

        public double getFatGrams() {
            return fatGrams;
        }

        public void setFatGrams(double fatGrams) {
            this.fatGrams = fatGrams;
        }

        @Override
        public String toString() {
            return String.format("%d kcal | P: %.1fg, C: %.1fg, F: %.1fg", calories, proteinGrams, carbsGrams, fatGrams);
        }
    }

    public Menu() {
        this.nutrition = new NutritionInfo();
    }

    public Menu(Long menuId, LocalDate menuDate, String breakfast, String lunch, String snacks, String dinner, NutritionInfo nutrition) {
        this.menuId = menuId;
        this.menuDate = menuDate;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.snacks = snacks;
        this.dinner = dinner;
        this.nutrition = (nutrition != null) ? nutrition : new NutritionInfo();
    }

    public String getMealContent(MealType mealType) {
        if (mealType == null) return "";
        return switch (mealType) {
            case BREAKFAST -> breakfast;
            case LUNCH -> lunch;
            case SNACKS -> snacks;
            case DINNER -> dinner;
        };
    }

    public void setMealContent(MealType mealType, String content) {
        if (mealType == null) return;
        switch (mealType) {
            case BREAKFAST -> this.breakfast = content;
            case LUNCH -> this.lunch = content;
            case SNACKS -> this.snacks = content;
            case DINNER -> this.dinner = content;
        }
    }

    @Override
    public String toFormattedText() {
        return String.format("""
                MENU FOR DATE: %s
                ------------------------------------------------------------
                [BREAKFAST] (07:30 - 09:30): %s
                [LUNCH]     (12:00 - 14:00): %s
                [SNACKS]    (16:30 - 18:00): %s
                [DINNER]    (19:30 - 21:30): %s
                [NUTRITION]: %s
                ------------------------------------------------------------
                """, menuDate, breakfast, lunch, snacks, dinner, nutrition);
    }

    @Override
    public String toCsvRow() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%.1f,%.1f,%.1f",
                menuDate,
                escapeCsv(breakfast),
                escapeCsv(lunch),
                escapeCsv(snacks),
                escapeCsv(dinner),
                nutrition.getCalories(),
                nutrition.getProteinGrams(),
                nutrition.getCarbsGrams(),
                nutrition.getFatGrams());
    }

    @Override
    public String getCsvHeader() {
        return "Date,Breakfast,Lunch,Snacks,Dinner,Calories,Protein_g,Carbs_g,Fat_g";
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }

    // Getters and Setters
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public LocalDate getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(LocalDate menuDate) {
        this.menuDate = menuDate;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getSnacks() {
        return snacks;
    }

    public void setSnacks(String snacks) {
        this.snacks = snacks;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public NutritionInfo getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionInfo nutrition) {
        this.nutrition = nutrition;
    }

    @Override
    public String toString() {
        return String.format("Menu[Date=%s, B='%s', L='%s', S='%s', D='%s']", menuDate, breakfast, lunch, snacks, dinner);
    }
}
