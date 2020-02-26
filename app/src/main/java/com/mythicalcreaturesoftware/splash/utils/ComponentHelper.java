package com.mythicalcreaturesoftware.splash.utils;

import javafx.scene.image.ImageView;
import reader.model.Dimension;

public class ComponentHelper {

    public static void setImageViewSize (ImageView imageView, Dimension dimension, double scale) {
        if (dimension != null) {
            imageView.setFitWidth(dimension.getWidth() * scale);
            imageView.setFitHeight(dimension.getHeight() * scale);
        }
    }
}
