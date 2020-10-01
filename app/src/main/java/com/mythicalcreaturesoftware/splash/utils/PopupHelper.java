package com.mythicalcreaturesoftware.splash.utils;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopupHelper {

    private static Logger logger = LoggerFactory.getLogger(PopupHelper.class);

    public static void showErrorPopup(Scene scene, String message, String detail) {
        Alert dlg = createAlert(scene, Alert.AlertType.ERROR);
        dlg.setTitle("Error");

        final TextArea textArea = new TextArea(detail);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        dlg.getDialogPane().setExpandableContent(expContent);
        configureSampleDialog(scene, dlg, message);
        showDialog(scene, dlg);
    }

    private static Alert createAlert(Scene scene, Alert.AlertType type) {
        Window owner = scene.getWindow();

        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.initOwner(owner);
        dlg.setResizable(true);

        return dlg;
    }

    private static void configureSampleDialog(Scene scene, Dialog<?> dlg, String header) {
        Window owner = scene.getWindow();

        dlg.getDialogPane().setHeaderText(header);
        dlg.getDialogPane().setGraphic(new MaterialDesignIconView(MaterialDesignIcon.ALERT, "40"));
        dlg.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dlg.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);

        dlg.initOwner(owner);
    }

    private static void showDialog(Scene scene, Dialog<?> dlg) {
        Window owner = scene.getWindow();

        dlg.initOwner(owner);

        dlg.show();
    }
}
