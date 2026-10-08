package com.example.nextnest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ThemeManager {
    private static ThemeManager instance;
    private final StringProperty currentTheme;

    private ThemeManager() {
        // Default theme is 'Deep Sea'
        currentTheme = new SimpleStringProperty("#2D3447");
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public StringProperty currentThemeProperty() {
        return currentTheme;
    }

    public String getCurrentTheme() {
        return currentTheme.get();
    }

    public void setCurrentTheme(String theme) {
        currentTheme.set(theme);
    }
}

