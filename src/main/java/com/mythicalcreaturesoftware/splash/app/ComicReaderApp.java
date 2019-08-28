package com.mythicalcreaturesoftware.splash.app;

import com.mythicalcreaturesoftware.splash.ui.ComicReaderView;
import com.mythicalcreaturesoftware.splash.ui.LoadingView;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.ComicReaderViewModel;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.LoadingViewModel;
import com.mythicalcreaturesoftware.splash.utils.ResizeHelper;
import com.mythicalcreaturesoftware.splash.utils.ScreenHelper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ComicReaderApp extends Application {

    private static Logger logger = LoggerFactory.getLogger(ComicReaderApp.class);

    private static ComicReaderApp instance;

    private ViewTuple<LoadingView, LoadingViewModel> loadingViewTuple;
    private ViewTuple<ComicReaderView, ComicReaderViewModel> readerViewTuple;
    private StackPane stackPane;

    public static ComicReaderApp instance() {
        return instance;
    }

    public ComicReaderApp() {
        ComicReaderApp.instance = this;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("default");
            MvvmFX.setGlobalResourceBundle(bundle);

            stackPane = new StackPane();
            stackPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

            loadingViewTuple = FluentViewLoader.fxmlView(LoadingView.class).load();
            Parent loadingRoot = loadingViewTuple.getView();
            loadingRoot.setVisible(false);

            readerViewTuple = FluentViewLoader.fxmlView(ComicReaderView.class).load();
            Parent root = readerViewTuple.getView();

            stackPane.getChildren().add(loadingRoot);
            stackPane.getChildren().add(root);

            Scene scene = new Scene(stackPane);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(bundle.getString("window.title"));

            ScreenHelper.maximize(primaryStage);
            ResizeHelper.addResizeListener(primaryStage);

            stackPane.prefHeightProperty().bind(primaryStage.heightProperty());
            stackPane.prefWidthProperty().bind(primaryStage.widthProperty());

            primaryStage.show();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void showLoading() {
        loadingViewTuple.getView().setVisible(true);
        setNodeToFront(loadingViewTuple.getView());
    }

    public void hideLoading() {
        loadingViewTuple.getView().setVisible(false);
        setNodeToFront(readerViewTuple.getView());
    }

    private void setNodeToFront(Node node) {
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(stackPane.getChildren());
        Collections.sort(workingCollection, Comparator.comparing(node::equals));
        stackPane.getChildren().setAll(workingCollection);
    }
}
