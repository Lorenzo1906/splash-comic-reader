package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import com.mythicalcreaturesoftware.splash.service.impl.FileServiceImpl;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import com.mythicalcreaturesoftware.splash.utils.MathHelper;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;

public class FullscreenViewModel implements ViewModel {

    private static Logger logger = LoggerFactory.getLogger(LoadingViewModel.class);

    private Command openFileCommand;

    private IntegerProperty currentPageProperty;
    private IntegerProperty totalPagesProperty;

    private DoubleProperty scaleLevelProperty;
    private DoubleProperty currentPageDefaultScaleLevelProperty;
    private DoubleProperty screenWidthProperty;
    private DoubleProperty screenHeightProperty;

    private ObjectProperty<Image> leftImageProperty;
    private ObjectProperty<Image> rightImageProperty;

    private ObjectProperty<Dimension> leftImageDimensionProperty;
    private ObjectProperty<Dimension> rightImageDimensionProperty;

    public FullscreenViewModel() {
        logger.info("Initializing fullscreen view model");

        initDefaultProperties();
        initCommands();
    }

    private void initDefaultProperties() {
        logger.info("Initializing default properties");

        currentPageProperty = new SimpleIntegerProperty(1);
        totalPagesProperty = new SimpleIntegerProperty(1);

        leftImageDimensionProperty = new SimpleObjectProperty<>(new Dimension(1, 1));
        rightImageDimensionProperty = new SimpleObjectProperty<>(new Dimension(1, 1));

        scaleLevelProperty = new SimpleDoubleProperty(1);
        currentPageDefaultScaleLevelProperty = new SimpleDoubleProperty(1);
        screenWidthProperty = new SimpleDoubleProperty(1);
        screenHeightProperty = new SimpleDoubleProperty(1);

        leftImageProperty = new SimpleObjectProperty<>(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));
        rightImageProperty = new SimpleObjectProperty<>(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));
    }

    private void initCommands() {
        logger.info("Initializing commands");

        openFileCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                loadImages();
                updateTotalPages();
                updateCurrentPage();
                calculateScale();
            }
        }, true);
    }

    public IntegerProperty getCurrentPageProperty() {
        return currentPageProperty;
    }

    public DoubleProperty getScaleLevelProperty() {
        return scaleLevelProperty;
    }

    public DoubleProperty getCurrentPageDefaultScaleLevelProperty() {
        return currentPageDefaultScaleLevelProperty;
    }

    public ObjectProperty<Image> getLeftImageProperty() {
        return leftImageProperty;
    }

    public ObjectProperty<Image> getRightImageProperty() {
        return rightImageProperty;
    }

    public IntegerProperty getTotalPagesProperty() {
        return totalPagesProperty;
    }

    public DoubleProperty getScreenWidthProperty() {
        return screenWidthProperty;
    }

    public DoubleProperty getScreenHeightProperty() {
        return screenHeightProperty;
    }

    public Command getOpenFileCommand() {
        return openFileCommand;
    }

    public ObjectProperty<Dimension> getLeftImageDimensionProperty() {
        return leftImageDimensionProperty;
    }

    public ObjectProperty<Dimension> getRightImageDimensionProperty() {
        return rightImageDimensionProperty;
    }

    private void loadImages () {
        logger.debug("Loading images");

        Platform.runLater(() -> leftImageProperty.setValue(new Image(FileServiceImpl.getInstance().getCurrentVerso(), true)));
        Platform.runLater(() -> rightImageProperty.setValue(new Image(FileServiceImpl.getInstance().getCurrentRecto(), true)));
    }

    private void updateTotalPages () {
        logger.debug("Updating total page");

        Platform.runLater(() -> totalPagesProperty.setValue(FileServiceImpl.getInstance().getTotalPages()));
    }

    private void updateCurrentPage() {
        logger.debug("Updating current page");

        Platform.runLater(() -> currentPageProperty.setValue(FileServiceImpl.getInstance().getCurrentPageNumber()));
    }

    private void calculateScale() {
        double maxHeight;

        Dimension leftImageDimension = FileServiceImpl.getInstance().getCurrentVersoSize();
        Dimension rightImageDimension = FileServiceImpl.getInstance().getCurrentRectoSize();

        maxHeight = Math.max(leftImageDimension.height, rightImageDimension.height);

        leftImageDimensionProperty.setValue(leftImageDimension);
        rightImageDimensionProperty.setValue(rightImageDimension);

        double defaultScaleLevel = (MathHelper.percentageOf(maxHeight, getScreenHeightProperty().getValue()))/100;
        Platform.runLater(() -> currentPageDefaultScaleLevelProperty.setValue(defaultScaleLevel));
        if (scaleLevelProperty.get() == 1) {
            Platform.runLater(() -> scaleLevelProperty.setValue(defaultScaleLevel));
        }
    }
}
