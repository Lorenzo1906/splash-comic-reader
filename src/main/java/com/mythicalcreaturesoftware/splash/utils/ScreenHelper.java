package com.mythicalcreaturesoftware.splash.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

public class ScreenHelper {

    private static double width = 800;
    private static double height = 600;
    private static double x = 0;
    private static double y = 0;
    private static boolean isMaximized = false;

    public static void maximize (Stage stage) {

        setScreenCurrentValues(stage);

        Screen screen = getCurrentScreen(stage);
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        isMaximized = true;
    }

    public static void unMaximize (Stage stage) {

        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);
        isMaximized = false;
    }

    public static boolean isMaximized() {
        return isMaximized;
    }

    private static void setScreenCurrentValues (Stage stage) {

        if (!Double.isNaN(stage.getWidth())) {
            width = stage.getWidth();
        }

        if (!Double.isNaN(stage.getHeight())) {
            height = stage.getHeight();
        }

        if (!Double.isNaN(stage.getX())) {
            x = stage.getX();
        }

        if (!Double.isNaN(stage.getY())) {
            y = stage.getY();
        }
    }

    private static Screen getCurrentScreen(Stage stage) {
        double currentX = 0d;
        if (!Double.isNaN(stage.getX())) {
            currentX = stage.getX();
        }

        Screen currentScreen = null;

        List<Screen> screens = Screen.getScreens();
        for (Screen screen : screens) {
            if (currentX >= screen.getBounds().getMinX() && currentX <= screen.getBounds().getMaxX()) {
                currentScreen = screen;
            }
        }

        return currentScreen;
    }
}
