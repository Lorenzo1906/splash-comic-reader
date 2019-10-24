package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.ui.viewmodel.FullscreenViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
    private static Logger logger = LoggerFactory.getLogger(ComicReaderView.class);

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

    @InjectViewModel
    private FullscreenViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing fullscreen view");

        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        double height = resolution.getHeight();

        imageContainer.setMaxHeight(height);
        leftImage.setFitHeight(height);
        rightImage.setFitHeight(height);
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
}
