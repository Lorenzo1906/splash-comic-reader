package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import com.mythicalcreaturesoftware.splash.service.FileService;
import com.mythicalcreaturesoftware.splash.service.impl.FileServiceImpl;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import com.mythicalcreaturesoftware.splash.utils.MathHelper;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.CompositeCommand;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;

public class ComicReaderViewModel implements ViewModel {
    private static Logger logger = LoggerFactory.getLogger(ComicReaderViewModel.class);

    private Command readingDirectionCommand;
    private Command zoomInCommand;
    private Command zoomOutCommand;
    private Command openFileCommand;
    private Command loadPreviousPageCommand;
    private Command loadNextPageCommand;
    private Command previousPageCommand;
    private Command nextPageCommand;
    private Command loadSliderPageCommand;
    private Command updatePagesPerViewPageCommand;
    private Command applyDefaultScaleCommand;
    private Command updatePreviewImageCommand;

    private BooleanProperty zoomInButton;
    private BooleanProperty zoomOutButton;
    private BooleanProperty readingDirectionRightProperty;
    private BooleanProperty isTwoPagesProperty;
    private BooleanProperty enableAll;
    private BooleanProperty enableNextPage;

    private IntegerProperty currentPageProperty;
    private IntegerProperty currentPagePreviewProperty;
    private IntegerProperty totalPagesProperty;

    private DoubleProperty scaleLevelProperty;
    private DoubleProperty screenWidthProperty;
    private DoubleProperty screenHeightProperty;
    private DoubleProperty currentPageDefaultScaleLevelProperty;

    private StringProperty fileNameProperty;
    private StringProperty filePathProperty;

    private ObjectProperty<Image> leftImageProperty;
    private ObjectProperty<Image> rightImageProperty;
    private ObjectProperty<Image> previewImageProperty;
    private ObjectProperty<Dimension> leftImageDimensionProperty;
    private ObjectProperty<Dimension> rightImageDimensionProperty;

    private FileService fileService;

    public ComicReaderViewModel () {
        logger.info("Initializing comic reader view model");

        fileService = new FileServiceImpl();

        initDefaultProperties();
        initCommands();
    }

    private void initDefaultProperties() {
        logger.info("Initializing default properties");

        isTwoPagesProperty = new SimpleBooleanProperty(true);
        readingDirectionRightProperty = new SimpleBooleanProperty(true);
        enableAll = new SimpleBooleanProperty(false);
        enableNextPage = new SimpleBooleanProperty(false);

        zoomInButton = new SimpleBooleanProperty(true);
        zoomOutButton = new SimpleBooleanProperty(true);

        scaleLevelProperty = new SimpleDoubleProperty(1);
        screenWidthProperty = new SimpleDoubleProperty(1);
        screenHeightProperty = new SimpleDoubleProperty(1);
        currentPageDefaultScaleLevelProperty = new SimpleDoubleProperty(1);

        currentPageProperty = new SimpleIntegerProperty(1);
        currentPagePreviewProperty = new SimpleIntegerProperty(1);
        totalPagesProperty = new SimpleIntegerProperty(1);

        fileNameProperty = new SimpleStringProperty("");
        filePathProperty = new SimpleStringProperty("");

        leftImageProperty = new SimpleObjectProperty<>(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));
        rightImageProperty = new SimpleObjectProperty<>(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));
        previewImageProperty = new SimpleObjectProperty<>(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));

        leftImageDimensionProperty = new SimpleObjectProperty<>(new Dimension(1, 1));
        rightImageDimensionProperty = new SimpleObjectProperty<>(new Dimension(1, 1));
    }

    private void initCommands() {
        logger.info("Initializing commands");

        zoomInCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                zoomIn();
            }
        }, zoomInButton, false);

        zoomOutCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                zoomOut();
            }
        }, zoomOutButton, false);

        applyDefaultScaleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                applyDefaultScale();
            }
        }, false);

        Command pagePerViewCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                setPagesPerView();
            }
        }, false);

        Command updateCurrentFromUiCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                updateCurrentFromUi();
            }
        }, false);

        Command loadImagesCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                loadImages();
            }
        }, false);

        Command calculateScaleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                calculateScale();
            }
        }, false);

        openFileCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                openFile();
                loadImages();
                updateTotalPages();
                updateCurrentPage();
                calculateScale();
            }
        }, true);

        previousPageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                previousPage();
            }
        }, createEnablePreviousPageButtonProperty(), false);

        nextPageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                nextPage();
            }
        }, createEnableNextPageButtonProperty(), false);

        updatePreviewImageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                updatePreviewImage();
            }
        }, false);

        loadNextPageCommand = new CompositeCommand(nextPageCommand, loadImagesCommand, calculateScaleCommand);

        loadPreviousPageCommand = new CompositeCommand(previousPageCommand, loadImagesCommand, calculateScaleCommand);

        loadSliderPageCommand = new CompositeCommand(updateCurrentFromUiCommand, loadImagesCommand, calculateScaleCommand);

        updatePagesPerViewPageCommand = new CompositeCommand(pagePerViewCommand, loadImagesCommand, calculateScaleCommand);

        Command changeReadingDirectionCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                changeReadingDirection();
            }
        }, false);

        readingDirectionCommand = new CompositeCommand(changeReadingDirectionCommand, loadImagesCommand, calculateScaleCommand);
    }

    private BooleanProperty createEnableNextPageButtonProperty () {
        logger.debug("Creating EnableNextPageButtonProperty");

        BooleanProperty enableNextPageButton = new SimpleBooleanProperty();

        BooleanBinding nextPageBinding = Bindings.when(enableNextPage).then(true).otherwise(false);
        enableNextPageButton.bind(nextPageBinding);

        return enableNextPageButton;
    }

    private BooleanProperty createEnablePreviousPageButtonProperty () {
        logger.debug("creating EnablePreviousPageButtonProperty");

        BooleanProperty enablePreviousPageButton = new SimpleBooleanProperty();

        BooleanBinding previousPageBinding = Bindings.when(currentPageProperty.lessThanOrEqualTo(1)).then(false).otherwise(true);
        enablePreviousPageButton.bind(previousPageBinding);

        return enablePreviousPageButton;
    }

    public StringProperty getFileNameProperty(){
        return fileNameProperty;
    }

    public StringProperty getFilePathProperty(){
        return filePathProperty;
    }

    public ObjectProperty<Image> getLeftImageProperty(){
        return leftImageProperty;
    }

    public ObjectProperty<Image> getRightImageProperty(){
        return rightImageProperty;
    }

    public ObjectProperty<Image> getPreviewImageProperty() {
        return previewImageProperty;
    }

    public ObjectProperty<Dimension> getLeftImageDimensionProperty(){
        return leftImageDimensionProperty;
    }

    public ObjectProperty<Dimension> getRightImageDimensionProperty(){
        return rightImageDimensionProperty;
    }

    public BooleanProperty getIsTwoPagesProperty(){
        return isTwoPagesProperty;
    }

    public BooleanProperty getReadingDirectionRightProperty(){
        return readingDirectionRightProperty;
    }

    public BooleanProperty getEnableAll() {
        return enableAll;
    }

    public DoubleProperty getScaleLevelProperty(){
        return scaleLevelProperty;
    }

    public DoubleProperty getScreenWidthProperty(){
        return screenWidthProperty;
    }

    public DoubleProperty getScreenHeightProperty(){
        return screenHeightProperty;
    }

    public DoubleProperty getCurrentPageDefaultScaleLevelProperty(){
        return currentPageDefaultScaleLevelProperty;
    }

    public IntegerProperty getCurrentPageProperty(){
        return currentPageProperty;
    }

    public IntegerProperty getCurrentPagePreviewProperty(){
        return currentPagePreviewProperty;
    }

    public IntegerProperty getTotalPagesProperty(){
        return totalPagesProperty;
    }

    public Command getLoadPreviousPageCommand() {
        return loadPreviousPageCommand;
    }

    public Command getLoadNextPageCommand() {
        return loadNextPageCommand;
    }

    public Command getReadingDirectionCommand() {
        return readingDirectionCommand;
    }

    public Command getUpdatePagesPerViewPageCommand() {
        return updatePagesPerViewPageCommand;
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

    public Command getOpenFileCommand() {
        return openFileCommand;
    }

    public Command getPreviousPageCommand() {
        return previousPageCommand;
    }

    public Command getNextPageCommand() {
        return nextPageCommand;
    }

    public Command getLoadSliderPageCommand() {
        return loadSliderPageCommand;
    }

    public Command getUpdatePreviewImageCommand() {
        return updatePreviewImageCommand;
    }

    private void previousPage() {
        logger.debug("Previous Page");

        fileService.updatePreviousPage(isTwoPagesProperty.getValue());
        currentPageProperty.setValue(fileService.getCurrentPageNumber());
    }

    private void nextPage() {
        logger.debug("Next Page");

        fileService.updateNextPage(isTwoPagesProperty.getValue());
        currentPageProperty.setValue(fileService.getCurrentPageNumber());
    }

    private void changeReadingDirection() {
        logger.debug("Change Reading Direction");

        readingDirectionRightProperty.setValue(!fileService.changeMangaMode());
        currentPageProperty.setValue(fileService.getCurrentPageNumber());
    }

    private void setPagesPerView() {
        logger.debug("Set pages per view");

        leftImageProperty.set(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));
        rightImageProperty.set(new Image(DefaultValuesHelper.DEFAULT_IMAGE_PATH, true));

        isTwoPagesProperty.setValue(!isTwoPagesProperty.getValue());
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

    private void openFile() {
        logger.debug("Opening file");

        String filename = fileService.loadFile(filePathProperty.getValue());
        Platform.runLater(() -> fileNameProperty.setValue(filename));
        Platform.runLater(() -> readingDirectionRightProperty.setValue(!fileService.getMangaMode()));
        enableAll.setValue(true);

        logger.debug("Finished opening file");
    }

    private void loadImages () {
        logger.debug("Loading images");

        if (isTwoPagesProperty.getValue()) {
            Platform.runLater(() -> leftImageProperty.setValue(new Image(fileService.getCurrentVerso(), true)));
            Platform.runLater(() -> rightImageProperty.setValue(new Image(fileService.getCurrentRecto(), true)));
        } else {
            Platform.runLater(() -> leftImageProperty.setValue(new Image(fileService.getCurrentPage(), true)));
        }

        Platform.runLater(() -> enableNextPage.setValue(fileService.canChangeToNextPage(isTwoPagesProperty.getValue())));
    }

    private void calculateScale() {
        double maxHeight;

        if (isTwoPagesProperty.getValue()) {
            Dimension leftImageDimension = fileService.getCurrentVersoSize();
            Dimension rightImageDimension = fileService.getCurrentRectoSize();

            maxHeight = Math.max(leftImageDimension.height, rightImageDimension.height);

            leftImageDimensionProperty.setValue(leftImageDimension);
            rightImageDimensionProperty.setValue(rightImageDimension);
        } else {
            Dimension imageDimension = fileService.getCurrentPageSize();

            maxHeight = imageDimension.height;

            leftImageDimensionProperty.setValue(imageDimension);
        }

        double defaultScaleLevel = (MathHelper.percentageOf(maxHeight, getScreenHeightProperty().getValue()))/100;
        Platform.runLater(() -> currentPageDefaultScaleLevelProperty.setValue(defaultScaleLevel));
        if (scaleLevelProperty.get() == 1) {
            Platform.runLater(() -> scaleLevelProperty.setValue(defaultScaleLevel));
        }
    }

    private void updateTotalPages () {
        logger.debug("Updating total page");

        Platform.runLater(() -> totalPagesProperty.setValue(fileService.getTotalPages()));
    }

    private void updateCurrentPage() {
        logger.debug("Updating current page");

        Platform.runLater(() -> currentPageProperty.setValue(fileService.getCurrentPageNumber()));
    }

    private void updateCurrentFromUi() {
        logger.debug("Updating current page");

        fileService.setCurrentPage(currentPageProperty.getValue());
    }

    private void updatePreviewImage() {
        logger.debug("Updating preview image");

        String path = fileService.getCurrentPreviewByPageNumber(currentPagePreviewProperty.getValue());
        previewImageProperty.set(new Image(path, true));
    }
}
