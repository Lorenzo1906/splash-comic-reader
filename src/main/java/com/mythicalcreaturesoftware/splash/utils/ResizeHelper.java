package com.mythicalcreaturesoftware.splash.utils;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeHelper {

    public static void addResizeListener(Stage stage) {
        ResizeListener resizeListener = new ResizeListener(stage);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
    }

    private static class ResizeListener implements EventHandler<MouseEvent> {
        private Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private int border = 4;
        private double startX = 0;
        private double startY = 0;
        private double startScreenX = 0;
        private double startScreenY = 0;

        public ResizeListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX();
            double mouseEventY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                mouseMoved(mouseEventX, mouseEventY, sceneWidth, sceneHeight, scene);
            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                mouseExited(scene);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                mousePressed(mouseEventX, mouseEventY);
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                mouseDragged(mouseEvent, mouseEventX, mouseEventY);
            }

            if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                mousePressedScreen(mouseEvent);
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                mouseDraggedScreen(mouseEvent);
            }
        }

        private void mouseMoved(double mouseEventX, double mouseEventY, double sceneWidth, double sceneHeight, Scene scene) {
            if (mouseEventX < border && mouseEventY < border) {
                cursorEvent = Cursor.NW_RESIZE;
            } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
                cursorEvent = Cursor.SW_RESIZE;
            } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                cursorEvent = Cursor.NE_RESIZE;
            } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                cursorEvent = Cursor.SE_RESIZE;
            } else if (mouseEventX < border) {
                cursorEvent = Cursor.W_RESIZE;
            } else if (mouseEventX > sceneWidth - border) {
                cursorEvent = Cursor.E_RESIZE;
            } else if (mouseEventY < border) {
                cursorEvent = Cursor.N_RESIZE;
            } else if (mouseEventY > sceneHeight - border) {
                cursorEvent = Cursor.S_RESIZE;
            } else {
                cursorEvent = Cursor.DEFAULT;
            }
            scene.setCursor(cursorEvent);
        }

        private void mouseExited(Scene scene) {
            scene.setCursor(Cursor.DEFAULT);
        }

        private void mousePressed(double mouseEventX, double mouseEventY) {
            startX = stage.getWidth() - mouseEventX;
            startY = stage.getHeight() - mouseEventY;
        }

        private void mouseDragged(MouseEvent mouseEvent, double mouseEventX, double mouseEventY) {
            if (!Cursor.DEFAULT.equals(cursorEvent)) {
                if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                    heightResize(mouseEvent, mouseEventY);
                }

                if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                    widthResize(mouseEvent, mouseEventX);
                }
            }
        }

        private void widthResize(MouseEvent mouseEvent, double mouseEventX) {
            double minWidth = stage.getMinWidth() > (border * 2) ? stage.getMinWidth() : (border * 2);
            double maxWidth = stage.getMaxWidth();
            if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {
                double newWidth = stage.getWidth() - (mouseEvent.getScreenX() - stage.getX());
                if (newWidth >= minWidth && newWidth <= maxWidth) {
                    stage.setWidth(newWidth);
                    stage.setX(mouseEvent.getScreenX());
                } else {
                    newWidth = Math.min(Math.max(newWidth, minWidth), maxWidth);
                    // x1 + w1 = x2 + w2
                    // x1 = x2 + w2 - w1
                    stage.setX(stage.getX() + stage.getWidth() - newWidth);
                    stage.setWidth(newWidth);
                }
            } else {
                stage.setWidth(Math.min(Math.max(mouseEventX + startX, minWidth), maxWidth));
            }
        }

        private void heightResize(MouseEvent mouseEvent, double mouseEventY) {
            double minHeight = stage.getMinHeight() > (border * 2) ? stage.getMinHeight() : (border * 2);
            double maxHeight = stage.getMaxHeight();
            if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {
                double newHeight = stage.getHeight() - (mouseEvent.getScreenY() - stage.getY());
                if (newHeight >= minHeight && newHeight <= maxHeight) {
                    stage.setHeight(newHeight);
                    stage.setY(mouseEvent.getScreenY());
                } else {
                    newHeight = Math.min(Math.max(newHeight, minHeight), maxHeight);
                    // y1 + h1 = y2 + h2
                    // y1 = y2 + h2 - h1
                    stage.setY(stage.getY() + stage.getHeight() - newHeight);
                    stage.setHeight(newHeight);
                }
            } else {
                stage.setHeight(Math.min(Math.max(mouseEventY + startY, minHeight), maxHeight));
            }
        }

        private void mousePressedScreen(MouseEvent mouseEvent) {
            startScreenX = mouseEvent.getScreenX();
            startScreenY = mouseEvent.getScreenY();
        }

        private void mouseDraggedScreen(MouseEvent mouseEvent) {
            if (Cursor.DEFAULT.equals(cursorEvent)) {
                stage.setX(stage.getX() + mouseEvent.getScreenX() - startScreenX);
                startScreenX = mouseEvent.getScreenX();
                stage.setY(stage.getY() + mouseEvent.getScreenY() - startScreenY);
                startScreenY = mouseEvent.getScreenY();
            }
        }
    }
}