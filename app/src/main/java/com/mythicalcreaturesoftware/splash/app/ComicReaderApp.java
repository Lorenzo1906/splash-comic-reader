package com.mythicalcreaturesoftware.splash.app;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.mythicalcreaturesoftware.splash.event.ActionMessageEvent;
import com.mythicalcreaturesoftware.splash.event.MessageEvent;
import com.mythicalcreaturesoftware.splash.ui.ComicReaderView;
import com.mythicalcreaturesoftware.splash.ui.FullscreenView;
import com.mythicalcreaturesoftware.splash.ui.LoadingView;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.ComicReaderViewModel;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.FullscreenViewModel;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.LoadingViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

public class ComicReaderApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ComicReaderApp.class);

    private static ComicReaderApp instance;

    private ViewTuple<LoadingView, LoadingViewModel> loadingViewTuple;
    private ViewTuple<ComicReaderView, ComicReaderViewModel> readerViewTuple;
    private ViewTuple<FullscreenView, FullscreenViewModel> fullscreenViewTuple;
    private StackPane stackPane;
    private BorderlessScene scene;
    private String path = "";

    public static ComicReaderApp instance() {
        return instance;
    }

    public ComicReaderApp() {
        ComicReaderApp.instance = this;
    }

    @Override
    public void start(Stage primaryStage) {

        processParams();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("default");
            MvvmFX.setGlobalResourceBundle(bundle);

            stackPane = new StackPane();
            stackPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            stackPane.getStyleClass().add("root");

            loadingViewTuple = FluentViewLoader.fxmlView(LoadingView.class).load();
            Parent loadingRoot = loadingViewTuple.getView();
            loadingRoot.setVisible(false);

            fullscreenViewTuple = FluentViewLoader.fxmlView(FullscreenView.class).load();
            Parent fullscreenRoot = fullscreenViewTuple.getView();
            fullscreenRoot.setVisible(false);

            readerViewTuple = FluentViewLoader.fxmlView(ComicReaderView.class).load();
            Parent root = readerViewTuple.getView();

            stackPane.getChildren().add(fullscreenRoot);
            stackPane.getChildren().add(loadingRoot);
            stackPane.getChildren().add(root);

            scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED, stackPane, 250, 250);
            scene.setMoveControl(readerViewTuple.getCodeBehind().getMainBar());

            URL css = getClass().getClassLoader().getResource("style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            setKeysShortcuts();
            readerViewTuple.getCodeBehind().setActive(true);

            primaryStage.setScene(scene);
            primaryStage.setTitle(bundle.getString("window.title"));
            primaryStage.setFullScreenExitHint("");

            stackPane.prefHeightProperty().bind(primaryStage.heightProperty());
            stackPane.prefWidthProperty().bind(primaryStage.widthProperty());

            primaryStage.show();

            primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    hideFullscreen();
                }
            });

            scene.maximizeStage();
            scene.removeDefaultCSS();

            if (path != null && !path.isEmpty()) {
                readerViewTuple.getCodeBehind().openFile(path);
            }
        } catch (Exception e) {
            logger.error("error starting app", e);
        }
    }

    private void processParams() {

        Map<String, String> params = this.getParameters().getNamed();
        path = params.get("file");
        if (path != null && !path.isEmpty()) {
            path = path.replaceFirst("^~", Matcher.quoteReplacement(System.getProperty("user.home")));
        }
    }

    private void setKeysShortcuts() {
        KeyCombination headerKeyCombination = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCombination previousPageKeyCombination = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_ANY);
        KeyCombination nextPageKeyCombination = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_ANY);
        KeyCombination readingDirectionKeyCombination = new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_ANY);
        KeyCombination pagePerViewKeyCombination = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_ANY);
        KeyCombination zoomOutKeyCombination = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_ANY);
        KeyCombination zoomInKeyCombination = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_ANY);
        KeyCombination defaultScaleKeyCombination = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_ANY);
        KeyCombination fullscreenKeyCombination = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_ANY);

        scene.setOnKeyPressed(event -> {
            if (headerKeyCombination.match(event)) {
                fireEvent(MessageEvent.OPEN_FILE_EVENT);
            }
            if (previousPageKeyCombination.match(event)) {
                fireEvent(MessageEvent.PREVIOUS_PAGE_EVENT);
            }
            if (nextPageKeyCombination.match(event)) {
                fireEvent(MessageEvent.NEXT_PAGE_EVENT);
            }
            if (readingDirectionKeyCombination.match(event)) {
                fireEvent(MessageEvent.CHANGE_READING_DIRECTION_EVENT);
            }
            if (pagePerViewKeyCombination.match(event)) {
                fireEvent(MessageEvent.CHANGE_PAGES_PER_VIEW_EVENT);
            }
            if (zoomOutKeyCombination.match(event)) {
                fireEvent(MessageEvent.ZOOM_OUT_EVENT);
            }
            if (zoomInKeyCombination.match(event)) {
                fireEvent(MessageEvent.ZOOM_IN_EVENT);
            }
            if (defaultScaleKeyCombination.match(event)) {
                fireEvent(MessageEvent.SET_DEFAULT_SCALE_EVENT);
            }
            if (fullscreenKeyCombination.match(event)) {
                fireEvent(MessageEvent.FULLSCREEN_EVENT);
            }
        });
    }

    public void fireEvent(EventType<MessageEvent> type) {
        Event.fireEvent(scene, new ActionMessageEvent(type));
    }

    public void showFullscreen() {
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.setFullScreen(true);

        fullscreenViewTuple.getView().setVisible(true);
        fullscreenViewTuple.getCodeBehind().playHintFadeAnimation();
        fullscreenViewTuple.getCodeBehind().playPageFadeAnimation();
        fullscreenViewTuple.getCodeBehind().setActive(true);
        readerViewTuple.getCodeBehind().setActive(false);

        setNodeToFront(fullscreenViewTuple.getView());

        fireEvent(MessageEvent.ENTERED_FULLSCREEN_EVENT);
    }

    private void hideFullscreen() {
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.setFullScreen(false);

        fullscreenViewTuple.getView().setVisible(false);
        fullscreenViewTuple.getCodeBehind().setActive(false);
        readerViewTuple.getCodeBehind().setActive(true);
        setNodeToFront(readerViewTuple.getView());

        fireEvent(MessageEvent.EXITED_FULLSCREEN_EVENT);
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
        workingCollection.sort(Comparator.comparing(node::equals));
        stackPane.getChildren().setAll(workingCollection);
    }
}
