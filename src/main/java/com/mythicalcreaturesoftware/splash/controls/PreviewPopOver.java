package com.mythicalcreaturesoftware.splash.controls;

import com.mythicalcreaturesoftware.splash.utils.MathHelper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.awt.*;

public class PreviewPopOver extends PopOver {

    private ImageView imageView;
    private ObjectProperty<Dimension> sizeProperty;
    private DoubleProperty scaleLevelProperty;

    public PreviewPopOver() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(imageView);

        setContentNode(vBox);
        setArrowLocation(ArrowLocation.BOTTOM_LEFT);

        scaleLevelProperty = new SimpleDoubleProperty(1);
        scaleLevelProperty.addListener((observable, oldValue, newValue) -> {
            imageView.setFitWidth(imageView.getFitWidth() * newValue.doubleValue());
            imageView.setFitHeight(imageView.getFitHeight() * newValue.doubleValue());
        });

        sizeProperty = new SimpleObjectProperty<>(new Dimension(1, 1));
        sizeProperty.addListener((observable, oldValue, newValue) -> {
            double fixedWidth = MathHelper.percentageValue(45, newValue.width);
            double fixedHeight = MathHelper.percentageValue(45, newValue.height);

            imageView.setFitWidth(fixedWidth);
            imageView.setFitHeight(fixedHeight);
        });
    }

    public  ImageView getImageView() {
        return imageView;
    }

    public ObjectProperty<Dimension> getSizeProperty() {
        return sizeProperty;
    }

    public DoubleProperty getScaleLevelProperty() {
        return scaleLevelProperty;
    }
}
