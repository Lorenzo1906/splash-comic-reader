package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.ui.viewmodel.LoadingViewModel;
import com.mythicalcreaturesoftware.splash.utils.DefaultValues;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * animation icon 'spinner' is provided by loading.io
 */
public class LoadingView implements FxmlView<LoadingViewModel>, Initializable {
    private static Logger logger = LoggerFactory.getLogger(ComicReaderView.class);

    @FXML
    private ImageView loadingImage;

    @InjectViewModel
    private LoadingViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing loading view");

        List<Image> images = new ArrayList<>();
        for (int i = 0; i <= 29; i++) {
            images.add(new Image(DefaultValues.SPINNER_IMAGE_PATH.replace("x", String.valueOf(i))));
        }

        Transition animation = new Transition(30) {
            {
                setCycleDuration(Duration.INDEFINITE);
            }

            int imageIndex = 0;
            @Override
            protected void interpolate(double fraction) {
                loadingImage.setImage(images.get(imageIndex));

                if (imageIndex < images.size() - 1) {
                    imageIndex++;
                } else {
                    imageIndex = 0;
                }
            }
        };
        animation.play();
    }
}
