package com.mythicalcreaturesoftware.splash.ui;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComicReaderViewModel implements ViewModel {
    private static Logger logger = LoggerFactory.getLogger(ComicReaderViewModel.class);

    private Command openFileCommand;
    private Command previousPageCommand;
    private Command nextPageCommand;
    private Command readingDirectionCommand;
    private Command pagePerViewCommand;
    private Command zoomInCommand;
    private Command zoomOutCommand;

    private BooleanProperty zoomInButton = new SimpleBooleanProperty();
    private BooleanProperty zoomOutButton = new SimpleBooleanProperty();
    private BooleanProperty readingDirectionRightProperty;
    private BooleanProperty isTwoPagesProperty;

    private IntegerProperty zoomLevelProperty;
    private IntegerProperty currentPageProperty;
    private IntegerProperty totalPagesProperty;

    private StringProperty fileNameProperty;
    private StringProperty filePathProperty;

    public ComicReaderViewModel () {
        logger.info("Initializing comic reader view model");

        initializeDefaultProperties();
        initializeCommands();
    }

    private void initializeDefaultProperties () {
        logger.info("Initializing default properties");

        isTwoPagesProperty = new SimpleBooleanProperty(false);
        readingDirectionRightProperty = new SimpleBooleanProperty(true);

        zoomInButton.bind(new SimpleBooleanProperty(true));
        zoomOutButton.bind(new SimpleBooleanProperty(true));

        zoomLevelProperty = new SimpleIntegerProperty(100);
        currentPageProperty = new SimpleIntegerProperty(1);
        totalPagesProperty = new SimpleIntegerProperty(15);

        fileNameProperty = new SimpleStringProperty("");
        filePathProperty = new SimpleStringProperty("");
    }

    private void initializeCommands () {
        logger.info("Initializing commands");

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

        openFileCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                openFile();
            }
        }, false);
    }

    private BooleanProperty createEnableNextPageButtonProperty () {
        logger.debug("Creating EnableNextPageButtonProperty");

        BooleanProperty enableNextPageButton = new SimpleBooleanProperty();

        BooleanBinding nextPageBinding = Bindings.when(currentPageProperty.greaterThanOrEqualTo(totalPagesProperty.getValue())).then(false).otherwise(true);
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

    BooleanProperty getIsTwoPagesProperty(){
        return isTwoPagesProperty;
    }

    BooleanProperty getReadingDirectionRightProperty(){
        return readingDirectionRightProperty;
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

    Command getOpenFileCommand() {
        return openFileCommand;
    }

    Command getPreviousPageCommand() {
        return previousPageCommand;
    }

    Command getNextPageCommand() {
        return nextPageCommand;
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

    private void previousPage() {
        logger.debug("Previous Page");
        currentPageProperty.setValue(currentPageProperty.getValue() - 1);
    }

    private void nextPage() {
        logger.debug("Next Page");
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
        fileNameProperty.setValue(filePathProperty.getValue());
    }
}
