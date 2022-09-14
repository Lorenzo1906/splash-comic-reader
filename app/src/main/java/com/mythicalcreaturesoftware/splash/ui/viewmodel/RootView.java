package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.mythicalcreaturesoftware.splash.app.ComicReaderApp;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public abstract class RootView {
    protected boolean isActive = false;

    @FXML
    protected VBox wrapper;

    @FXML
    protected AnchorPane mainBar;

    @FXML
    protected Button headerButton;

    @FXML
    protected Button libraryButton;

    @FXML
    protected Button readerButton;

    @FXML
    protected Button minimizeButton;

    @FXML
    protected Button maximizeButton;

    @FXML
    public abstract void openFileAction();

    public void setActive(boolean active) {
        isActive = active;
    }

    @FXML
    public void libraryAction() {
        ComicReaderApp.instance().showLibrary();
    }

    @FXML
    public void readerAction() {
        ComicReaderApp.instance().showReader();
    }

    @FXML
    public void minimizeAction() {
        BorderlessScene scene = (BorderlessScene) minimizeButton.getScene();
        scene.minimizeStage();
    }

    @FXML
    public void maximizeAction() {
        BorderlessScene scene = (BorderlessScene) maximizeButton.getScene();
        scene.maximizeStage();
    }

    public Node getMainBar() {
        return mainBar;
    }

    @FXML
    public void closeAction() {
        System.exit(0);
    }
}
