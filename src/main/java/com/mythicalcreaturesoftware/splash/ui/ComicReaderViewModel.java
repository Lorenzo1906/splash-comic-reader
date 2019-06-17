package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.service.FileService;
import com.mythicalcreaturesoftware.splash.service.impl.FileServiceImpl;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.CompositeCommand;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComicReaderViewModel implements ViewModel {
    private static Logger logger = LoggerFactory.getLogger(ComicReaderViewModel.class);

    private Command readingDirectionCommand;
    private Command pagePerViewCommand;
    private Command zoomInCommand;
    private Command zoomOutCommand;
    private Command openFileCompleteCommand;
    private Command loadPreviousPageCommand;
    private Command loadNextPageCommand;
    private Command previousPageCommand;
    private Command nextPageCommand;

    private BooleanProperty zoomInButton = new SimpleBooleanProperty();
    private BooleanProperty zoomOutButton = new SimpleBooleanProperty();
    private BooleanProperty readingDirectionRightProperty;
    private BooleanProperty isTwoPagesProperty;
    private BooleanProperty enableAll;

    private IntegerProperty zoomLevelProperty;
    private IntegerProperty currentPageProperty;
    private IntegerProperty totalPagesProperty;

    private StringProperty fileNameProperty;
    private StringProperty filePathProperty;

    private ObjectProperty<Image> leftImageProperty;

    private FileService fileService;

    public ComicReaderViewModel () {
        logger.info("Initializing comic reader view model");

        fileService = new FileServiceImpl();

        initializeDefaultProperties();
        initializeCommands();
    }

    private void initializeDefaultProperties () {
        logger.info("Initializing default properties");

        isTwoPagesProperty = new SimpleBooleanProperty(false);
        readingDirectionRightProperty = new SimpleBooleanProperty(true);
        enableAll = new SimpleBooleanProperty(false);

        zoomInButton.bind(new SimpleBooleanProperty(true));
        zoomOutButton.bind(new SimpleBooleanProperty(true));

        zoomLevelProperty = new SimpleIntegerProperty(100);
        currentPageProperty = new SimpleIntegerProperty(1);
        totalPagesProperty = new SimpleIntegerProperty(1);

        fileNameProperty = new SimpleStringProperty("");
        filePathProperty = new SimpleStringProperty("");

        leftImageProperty = new SimpleObjectProperty<>(new Image("https://www.hawtcelebs.com/wp-content/uploads/2018/05/melissa-benoist-chyler-leigh-amy-jacnkson-and-erica-durance-on-the-set-of-supergirl-in-vancouver-05-02-2018-7.jpg", true));
    }

    private void initializeCommands () {
        logger.info("Initializing commands");

        readingDirectionCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                changeReadingDirection();
            }
        }, false);

        pagePerViewCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                setPagesPerView();
            }
        }, false);

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

        Command loadImagesCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                loadImages();
            }
        }, false);

        Command openFileCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                openFile();
            }
        }, false);

        Command totalPageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
               updateTotalPages();
            }
        }, false);

        Command currentPageCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                updateCurrentPage();
            }
        }, false);

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

        openFileCompleteCommand = new CompositeCommand(openFileCommand, loadImagesCommand, totalPageCommand, currentPageCommand);

        loadNextPageCommand = new CompositeCommand(getNextPageCommand(), loadImagesCommand);

        loadPreviousPageCommand = new CompositeCommand(getPreviousPageCommand(), loadImagesCommand);
    }

    private BooleanProperty createEnableNextPageButtonProperty () {
        logger.debug("Creating EnableNextPageButtonProperty");

        BooleanProperty enableNextPageButton = new SimpleBooleanProperty();

        BooleanBinding nextPageBinding = Bindings.when(currentPageProperty.greaterThanOrEqualTo(totalPagesProperty)).then(false).otherwise(true);
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

    StringProperty getFileNameProperty(){
        return fileNameProperty;
    }

    StringProperty getFilePathProperty(){
        return filePathProperty;
    }

    ObjectProperty<Image> getLeftImageProperty(){
        return leftImageProperty;
    }

    BooleanProperty getIsTwoPagesProperty(){
        return isTwoPagesProperty;
    }

    BooleanProperty getReadingDirectionRightProperty(){
        return readingDirectionRightProperty;
    }

    BooleanProperty getEnableAll() {
        return enableAll;
    }

    IntegerProperty getZoomLevelProperty(){
        return zoomLevelProperty;
    }

    IntegerProperty getCurrentPageProperty(){
        return currentPageProperty;
    }

    IntegerProperty getTotalPagesProperty(){
        return totalPagesProperty;
    }

    Command getLoadPreviousPageCommand() {
        return loadPreviousPageCommand;
    }

    Command getLoadNextPageCommand() {
        return loadNextPageCommand;
    }

    Command getReadingDirectionCommand() {
        return readingDirectionCommand;
    }

    Command getPagePerViewCommand() {
        return pagePerViewCommand;
    }

    Command getZoomInCommand() {
        return zoomInCommand;
    }

    Command getZoomOutCommand() {
        return zoomOutCommand;
    }

    Command getOpenFileCompleteCommand() {
        return openFileCompleteCommand;
    }

    public Command getPreviousPageCommand() {
        return previousPageCommand;
    }

    public Command getNextPageCommand() {
        return nextPageCommand;
    }

    private void previousPage() {
        logger.debug("Previous Page");

        fileService.setCurrentPage(currentPageProperty.getValue() - 1);
        currentPageProperty.setValue(currentPageProperty.getValue() - 1);
    }

    private void nextPage() {
        logger.debug("Next Page");

        fileService.setCurrentPage(currentPageProperty.getValue() + 1);
        currentPageProperty.setValue(currentPageProperty.getValue() + 1);
    }

    private void changeReadingDirection() {
        logger.debug("Change Reading Direction");

        readingDirectionRightProperty.setValue(!readingDirectionRightProperty.getValue());
    }

    private void setPagesPerView() {
        logger.debug("Set pages per view");

        isTwoPagesProperty.setValue(!isTwoPagesProperty.getValue());
    }

    private void zoomIn() {
        logger.debug("Zooming in image");

        zoomLevelProperty.setValue(zoomLevelProperty.getValue() + 1);
    }

    private void zoomOut() {
        logger.debug("Zooming out image");

        zoomLevelProperty.setValue(zoomLevelProperty.getValue() - 1);
    }

    private void openFile() {
        logger.debug("Opening file");

        fileNameProperty.setValue(fileService.loadFile(filePathProperty.getValue()));
        enableAll.setValue(true);
    }

    private void loadImages () {
        logger.debug("Loading images");

        leftImageProperty.set(new Image(fileService.getCurrentPath(), true));
    }

    private void updateTotalPages () {
        logger.debug("Updating total page");

        totalPagesProperty.setValue(fileService.getTotalPages());
    }

    private void updateCurrentPage() {
        logger.debug("Updating current page");

        currentPageProperty.setValue(fileService.getCurrentPage());
    }
}
