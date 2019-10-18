package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.ui.viewmodel.FullscreenViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class FullscreenView implements FxmlView<FullscreenViewModel>, Initializable {
    private static Logger logger = LoggerFactory.getLogger(ComicReaderView.class);

    @FXML
    private StackPane wrapper;

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
}
