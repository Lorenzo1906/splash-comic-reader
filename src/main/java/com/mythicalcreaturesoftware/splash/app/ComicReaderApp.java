package com.mythicalcreaturesoftware.splash.app;

import com.mythicalcreaturesoftware.splash.ui.ComicReaderView;
import com.mythicalcreaturesoftware.splash.ui.ComicReaderViewModel;
import com.mythicalcreaturesoftware.splash.utils.ResizeHelper;
import com.mythicalcreaturesoftware.splash.utils.ScreenHelper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.ResourceBundle;

public class ComicReaderApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ResourceBundle bundle = ResourceBundle.getBundle("default");
        MvvmFX.setGlobalResourceBundle(bundle);

        ViewTuple<ComicReaderView, ComicReaderViewModel> viewTuple = FluentViewLoader.fxmlView(ComicReaderView.class).load();

        Parent root = viewTuple.getView();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle(bundle.getString("window.title"));

        ScreenHelper.maximize(primaryStage);
        ResizeHelper.addResizeListener(primaryStage);
        
        primaryStage.show();
    }
}
