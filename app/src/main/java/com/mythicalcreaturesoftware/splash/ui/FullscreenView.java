package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.event.MessageEvent;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.FullscreenViewModel;
import com.mythicalcreaturesoftware.splash.utils.ComponentHelper;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class FullscreenView implements FxmlView<FullscreenViewModel>, Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ComicReaderView.class);

    @FXML
    private HBox hintPanel;

    @FXML
    private AnchorPane pagePanel;

    @FXML
    private StackPane imageContainer;

    @FXML
    private ImageView leftImage;

    @FXML
    private ImageView rightImage;

    @FXML
    private Label pagePanelText;

    @InjectViewModel
    private FullscreenViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    private boolean isActive = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing fullscreen view");

        initImageViewer();
        initUiComponents();
        initScreen();
        initListeners();
    }

    private void initScreen() {
        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        double height = resolution.getHeight();
        double width = resolution.getWidth();

        viewModel.getScreenWidthProperty().set(width);
        viewModel.getScreenHeightProperty().set(height);
    }

    private void initUiComponents() {
        pagePanelText.textProperty().bind(Bindings.concat(resourceBundle.getString(DefaultValuesHelper.PAGE_TEXT_KEY), " ", viewModel.getCurrentPageProperty(), "/", viewModel.getTotalPagesProperty()));
    }

    private void initImageViewer() {
        leftImage.imageProperty().bind(viewModel.getLeftImageProperty());
        rightImage.imageProperty().bind(viewModel.getRightImageProperty());

        imageContainer.sceneProperty().addListener((observable, oldValue, newValue) -> {
            newValue.addEventFilter(MessageEvent.ENTERED_FULLSCREEN_EVENT, event -> {
                logger.debug("Receiving entered fullscreen event");

                if (isActive) {
                    viewModel.getRefreshFileCommand().execute();
                }
            });

            newValue.addEventFilter(MessageEvent.PREVIOUS_PAGE_EVENT, event -> {
                logger.debug("Receiving previous page event");

                if (isActive) {
                    viewModel.getLoadPreviousPageCommand().execute();
                    playPageFadeAnimation();
                }
            });

            newValue.addEventFilter(MessageEvent.NEXT_PAGE_EVENT, event -> {
                logger.debug("Receiving next page event");

                if (isActive) {
                    viewModel.getLoadNextPageCommand().execute();
                    playPageFadeAnimation();
                }
            });

            newValue.addEventFilter(MessageEvent.ZOOM_IN_EVENT, event -> {
                logger.debug("Receiving zoom in event");

                if (isActive) {
                    viewModel.getZoomInCommand().execute();
                }
            });

            newValue.addEventFilter(MessageEvent.ZOOM_OUT_EVENT, event -> {
                logger.debug("Receiving zoom out event");

                if (isActive) {
                    viewModel.getZoomOutCommand().execute();
                }
            });

            newValue.addEventFilter(MessageEvent.SET_DEFAULT_SCALE_EVENT, event -> {
                logger.debug("Receiving set default scale event");

                if (isActive) {
                    viewModel.getApplyDefaultScaleCommand().execute();
                }
            });
        });
    }

    private void initListeners() {
        viewModel.getScaleLevelProperty().addListener((observable, oldValue, newValue) -> {
            ComponentHelper.setImageViewSize(rightImage, viewModel.getRightImageDimensionProperty().get(), newValue.doubleValue());
            ComponentHelper.setImageViewSize(leftImage, viewModel.getLeftImageDimensionProperty().get(), newValue.doubleValue());
        });

        viewModel.getRightImageDimensionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ComponentHelper.setImageViewSize(rightImage, newValue, viewModel.getScaleLevelProperty().doubleValue());
            }
        });

        viewModel.getLeftImageDimensionProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue != null) {
                ComponentHelper.setImageViewSize(leftImage, newValue, viewModel.getScaleLevelProperty().doubleValue());
            }
        });
    }

    public void playHintFadeAnimation () {
        playFadeAnimation(hintPanel);
    }

    public void playPageFadeAnimation () {
        playFadeAnimation(pagePanel);
    }

    private void playFadeAnimation (Node node) {
        FadeTransition fade = new FadeTransition();

        fade.setDuration(Duration.millis(8000));
        fade.setFromValue(0.5);
        fade.setToValue(0.0);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);

        fade.setNode(node);

        fade.play();
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
