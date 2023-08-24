package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LibraryViewModel implements ViewModel {
    private static final Logger logger = LoggerFactory.getLogger(LibraryViewModel.class);

    public final static String OPEN_ALERT = "OPEN_ALERT";

    private final Command updateScannedFoldersListCommand;
    private final Command deleteIndexScannedFoldersListCommand;

    private final StringProperty valueToAddProperty;
    private final StringProperty valueToRemoveProperty;
    private final StringProperty defaultTextScannedFolderProperty;

    private final BooleanProperty seriesInstructionsVisible;
    private final BooleanProperty seriesVisible;

    private final ObjectProperty<ObservableList<String>> scannedFoldersList;
    private final ObjectProperty<ObservableList<String>> seriesList;

    public LibraryViewModel() {
        logger.info("Initializing library view model");

        valueToAddProperty = new SimpleStringProperty("");
        valueToRemoveProperty = new SimpleStringProperty("");
        defaultTextScannedFolderProperty = new SimpleStringProperty("");

        scannedFoldersList = new SimpleObjectProperty<>(FXCollections.observableArrayList(valueToAddProperty.get()));
        seriesList = new SimpleObjectProperty<>(FXCollections.observableArrayList(valueToAddProperty.get()));

        seriesInstructionsVisible = new SimpleBooleanProperty(true);
        seriesVisible = new SimpleBooleanProperty(false);

        updateScannedFoldersListCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                updateScannedFoldersList();
            }
        }, false);

        deleteIndexScannedFoldersListCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
               removeItemFromScannedFoldersList();
            }
        }, false);
    }

    private boolean listHaveDefaultValue() {
        boolean result = false;
        ObservableList<String> list = scannedFoldersList.get();

        Optional<String> firstItem = list.stream().findFirst();
        boolean isEmptyValue = firstItem.isPresent() && firstItem.get().isEmpty();
        boolean isDefaultValue = firstItem.isPresent() && firstItem.get().equals(defaultTextScannedFolderProperty.get());

        if (isEmptyValue || isDefaultValue) {
            result = true;
        }

        return result;
    }

    private void checkVisibilitySeries() {
        logger.debug("Checking visibility of series list");

        if (listHaveDefaultValue()) {
            seriesInstructionsVisible.set(true);
            seriesVisible.set(false);
        } else {
            seriesInstructionsVisible.set(false);
            seriesVisible.set(true);
        }
    }

    private void removeItemFromScannedFoldersList() {
        logger.debug("Removing folder to list");

        ObservableList<String> list = scannedFoldersList.get();

        list.removeAll(valueToRemoveProperty.get());

        if (list.size() == 0) {
            list.add(defaultTextScannedFolderProperty.get());
        }

        scannedFoldersList.set(list);
        checkVisibilitySeries();
    }

    private void updateScannedFoldersList() {
        logger.debug("Adding folder to list");

        ObservableList<String> list = scannedFoldersList.get();

        if (listHaveDefaultValue()) {
            list.set(0, valueToAddProperty.get());
        } else {
            list.add(valueToAddProperty.get());
        }

        scannedFoldersList.set(list);
        checkVisibilitySeries();
    }

    public ObjectProperty<ObservableList<String>> getScannedFoldersList() {
        return scannedFoldersList;
    }

    public ObjectProperty<ObservableList<String>> seriesListProperty() {
        return seriesList;
    }

    public StringProperty getValueToAddProperty() {
        return valueToAddProperty;
    }

    public StringProperty getValueToRemoveProperty() {
        return valueToRemoveProperty;
    }

    public StringProperty getDefaultTextScannedFolderProperty() {
        return defaultTextScannedFolderProperty;
    }

    public BooleanProperty seriesInstructionsVisibleProperty() {
        return seriesInstructionsVisible;
    }

    public BooleanProperty seriesVisibleProperty() {
        return seriesVisible;
    }

    public Command getUpdateScannedFoldersListCommand() {
        return updateScannedFoldersListCommand;
    }

    public Command getDeleteIndexScannedFoldersListCommand() {
        return deleteIndexScannedFoldersListCommand;
    }
}
