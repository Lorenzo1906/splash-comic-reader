package com.mythicalcreaturesoftware.splash.utils;

import javafx.scene.image.ImageView;

import java.awt.*;

public class ComponentHelper {

    public static void setImageViewSize (ImageView imageView, Dimension dimension, double scale) {
        if (dimension != null) {
            imageView.setFitWidth(dimension.width * scale);
            imageView.setFitHeight(dimension.height * scale);
        }
    }
}
