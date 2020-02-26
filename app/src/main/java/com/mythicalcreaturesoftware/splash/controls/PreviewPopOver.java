package com.mythicalcreaturesoftware.splash.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import reader.model.Dimension;
import reader.utils.MathUtilKt;

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
            imageView.setFitHeight(imageView.getFitHeight() * newValue.doubleValue());
        });

        sizeProperty = new SimpleObjectProperty<>(new Dimension(1, 1));
        sizeProperty.addListener((observable, oldValue, newValue) -> {
            double fixedHeight = MathUtilKt.percentageValue(30, newValue.getHeight());

            imageView.setFitHeight(fixedHeight);
        });
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ObjectProperty<Dimension> getSizeProperty() {
        return sizeProperty;
    }

    public DoubleProperty getScaleLevelProperty() {
        return scaleLevelProperty;
    }
}
