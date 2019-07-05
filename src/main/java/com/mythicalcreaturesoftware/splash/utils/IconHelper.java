package com.mythicalcreaturesoftware.splash.utils;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IconHelper {

    private static Logger logger = LoggerFactory.getLogger(IconHelper.class);

    public static ObjectProperty<Node> createDoublePageIconProperty () {
        logger.debug("Creating double icon property");

        HBox container = new HBox();

        MaterialDesignIconView rightIcon = new MaterialDesignIconView(MaterialDesignIcon.FILE, "18px");
        MaterialDesignIconView leftIcon = new MaterialDesignIconView(MaterialDesignIcon.FILE, "18px");

        container.getChildren().addAll(rightIcon, leftIcon);

        return new SimpleObjectProperty<>(container);
    }

    public static ImageView getNullImageView(){
        return null;
    }

    public static ObjectProperty<Node> createSimplePageIconProperty () {
        logger.debug("Creating single icon property");

        HBox container = new HBox();

        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FILE, "18px");

        container.getChildren().add(icon);

        return new SimpleObjectProperty<>(container);
    }

    public static ObjectProperty<Node> createReadingDirectionIconProperty (boolean inverse) {
        logger.debug("Creating reading direction icon property; value: " + inverse);

        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.IMPORT, "18px");

        if (inverse) {
            icon.setScaleX(-1);
        }

        return new SimpleObjectProperty<>(icon);
    }

    public static ObjectProperty<Node> createExpandScaleIconProperty () {
        logger.debug("Creating default expand scale icon property");

        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ARROW_EXPAND_ALL, "18px");

        return new SimpleObjectProperty<>(icon);
    }

    public static ObjectProperty<Node> createCollapseScaleIconProperty () {
        logger.debug("Creating default collapse scale icon property");

        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ARROW_COMPRESS_ALL, "18px");

        return new SimpleObjectProperty<>(icon);
    }
}
