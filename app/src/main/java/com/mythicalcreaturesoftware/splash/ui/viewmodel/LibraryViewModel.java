package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    private final ObjectProperty<ObservableList<String>> scannedFoldersList;

    public LibraryViewModel() {
        logger.info("Initializing library view model");

        valueToAddProperty = new SimpleStringProperty("");
        valueToRemoveProperty = new SimpleStringProperty("");
        defaultTextScannedFolderProperty = new SimpleStringProperty("");

        scannedFoldersList = new SimpleObjectProperty<>(FXCollections.observableArrayList(valueToAddProperty.get()));

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

    private void removeItemFromScannedFoldersList() {
        logger.info("Removing folder to list");

        ObservableList<String> list = scannedFoldersList.get();

        list.removeAll(valueToRemoveProperty.get());

        scannedFoldersList.set(list);
    }

    private void updateScannedFoldersList() {
        logger.info("Adding folder to list");

        ObservableList<String> list = scannedFoldersList.get();

        Optional<String> firstItem = list.stream().findFirst();
        boolean isEmptyValue = firstItem.isPresent() && firstItem.get().isEmpty();
        boolean isDefaultValue = firstItem.isPresent() && firstItem.get().equals(defaultTextScannedFolderProperty.get());

        if (isEmptyValue || isDefaultValue) {
            list.set(0, valueToAddProperty.get());
        } else {
            list.add(valueToAddProperty.get());
        }

        scannedFoldersList.set(list);
    }

    public ObjectProperty<ObservableList<String>> getScannedFoldersList() {
        return scannedFoldersList;
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

    public Command getUpdateScannedFoldersListCommand() {
        return updateScannedFoldersListCommand;
    }

    public Command getDeleteIndexScannedFoldersListCommand() {
        return deleteIndexScannedFoldersListCommand;
    }
}
