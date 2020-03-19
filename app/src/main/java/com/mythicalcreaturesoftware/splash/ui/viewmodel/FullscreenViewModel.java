package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.CompositeCommand;
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
import reader.model.Dimension;
import reader.service.impl.FileServiceImpl;
import reader.utils.MathUtilKt;

public class FullscreenViewModel implements ViewModel {

    private static Logger logger = LoggerFactory.getLogger(LoadingViewModel.class);

    private Command refreshFileCommand;
    private Command loadPreviousPageCommand;
    private Command loadNextPageCommand;
    private Command applyDefaultScaleCommand;
    private Command zoomInCommand;
    private Command zoomOutCommand;

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

        refreshFileCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                loadImages();
                updateTotalPages();
                updateCurrentPage();
                calculateScale();
            }
        }, true);

        Command previousPageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                previousPage();
            }
        }, false);

        Command nextPageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                nextPage();
            }
        },false);

        loadNextPageCommand = new CompositeCommand(nextPageCommand, refreshFileCommand);

        loadPreviousPageCommand = new CompositeCommand(previousPageCommand, refreshFileCommand);

        zoomInCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                zoomIn();
            }
        }, false);

        zoomOutCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                zoomOut();
            }
        }, false);

        applyDefaultScaleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                applyDefaultScale();
            }
        }, false);
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

    public Command getRefreshFileCommand() {
        return refreshFileCommand;
    }

    public Command getLoadPreviousPageCommand() {
        return loadPreviousPageCommand;
    }

    public Command getLoadNextPageCommand() {
        return loadNextPageCommand;
    }

    public ObjectProperty<Dimension> getLeftImageDimensionProperty() {
        return leftImageDimensionProperty;
    }

    public ObjectProperty<Dimension> getRightImageDimensionProperty() {
        return rightImageDimensionProperty;
    }

    public Command getZoomInCommand() {
        return zoomInCommand;
    }

    public Command getZoomOutCommand() {
        return zoomOutCommand;
    }

    public Command getApplyDefaultScaleCommand() {
        return applyDefaultScaleCommand;
    }

    private void loadImages () {
        logger.debug("Loading images");

        String leftImageValue = FileServiceImpl.INSTANCE.getCurrentVerso();
        String rightImageValue = FileServiceImpl.INSTANCE.getCurrentRecto();
        leftImageValue = leftImageValue.isEmpty() ? DefaultValuesHelper.DEFAULT_IMAGE_PATH : leftImageValue;
        rightImageValue = rightImageValue.isEmpty() ? DefaultValuesHelper.DEFAULT_IMAGE_PATH : rightImageValue;
        String finalLeftImageValue = leftImageValue;
        String finalRightImageValue = rightImageValue;

        Platform.runLater(() -> leftImageProperty.setValue(new Image(finalLeftImageValue, true)));
        Platform.runLater(() -> rightImageProperty.setValue(new Image(finalRightImageValue, true)));
    }

    private void updateTotalPages () {
        logger.debug("Updating total page");

        Platform.runLater(() -> totalPagesProperty.setValue(FileServiceImpl.INSTANCE.getTotalPages()));
    }

    private void updateCurrentPage() {
        logger.debug("Updating current page");

        Platform.runLater(() -> currentPageProperty.setValue(FileServiceImpl.INSTANCE.getCurrentPageNumber()));
    }

    private void calculateScale() {
        double maxHeight;

        Dimension leftImageDimension = FileServiceImpl.INSTANCE.getCurrentVersoSize();
        Dimension rightImageDimension = FileServiceImpl.INSTANCE.getCurrentRectoSize();

        maxHeight = Math.max(leftImageDimension.getHeight(), rightImageDimension.getHeight());

        leftImageDimensionProperty.setValue(leftImageDimension);
        rightImageDimensionProperty.setValue(rightImageDimension);

        double defaultScaleLevel = (MathUtilKt.percentageOf(maxHeight, getScreenHeightProperty().getValue()))/100;
        Platform.runLater(() -> currentPageDefaultScaleLevelProperty.setValue(defaultScaleLevel));
        if (scaleLevelProperty.get() == 1) {
            Platform.runLater(() -> scaleLevelProperty.setValue(defaultScaleLevel));
        }
    }

    private void previousPage() {
        logger.debug("Previous Page");

        FileServiceImpl.INSTANCE.updatePreviousPage(true);
        currentPageProperty.setValue(FileServiceImpl.INSTANCE.getCurrentPageNumber());
    }

    private void nextPage() {
        logger.debug("Next Page");

        FileServiceImpl.INSTANCE.updateNextPage(true);
        currentPageProperty.setValue(FileServiceImpl.INSTANCE.getCurrentPageNumber());
    }

    private void zoomIn() {
        logger.debug("Zooming in image");

        if (scaleLevelProperty.getValue() <= DefaultValuesHelper.MAXIMUM_SCALE_LEVEL) {
            scaleLevelProperty.setValue(scaleLevelProperty.getValue() + DefaultValuesHelper.SCALE_DELTA);
        }
    }

    private void zoomOut() {
        logger.debug("Zooming out image");

        if (scaleLevelProperty.getValue() >= DefaultValuesHelper.MINIMUM_SCALE_LEVEL) {
            scaleLevelProperty.setValue(scaleLevelProperty.getValue() - DefaultValuesHelper.SCALE_DELTA);
        }
    }

    private void applyDefaultScale() {
        logger.debug("Applying default scale");

        scaleLevelProperty.set(currentPageDefaultScaleLevelProperty.getValue());
    }
}
