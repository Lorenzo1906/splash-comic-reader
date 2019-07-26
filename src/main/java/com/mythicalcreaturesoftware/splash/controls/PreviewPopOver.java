package com.mythicalcreaturesoftware.splash.controls;

import com.mythicalcreaturesoftware.splash.utils.MathHelper;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class PreviewPopOver extends PopOver {

    private ImageView imageView;

    public PreviewPopOver(double width, double height) {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        double fixedWidth = MathHelper.percentageValue(30, width);
        double fixedHeight = MathHelper.percentageValue(30, height);

        imageView.setFitWidth(fixedWidth);
        imageView.setFitHeight(fixedHeight);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(imageView);

        setContentNode(vBox);
        setArrowLocation(ArrowLocation.BOTTOM_LEFT);
    }

    public  ImageView getImageView() {
        return imageView;
    }
}
