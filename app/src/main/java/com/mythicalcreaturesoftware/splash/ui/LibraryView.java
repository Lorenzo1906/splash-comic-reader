package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.ui.viewmodel.LibraryViewModel;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.RootView;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper.LIST_CELL_HEIGHT;

public class LibraryView extends RootView implements FxmlView<LibraryViewModel>, Initializable {

    private static final Logger logger = LoggerFactory.getLogger(LibraryView.class);

    @FXML
    protected VBox wrapper;

    @FXML
    protected ListView<String> scannedFoldersList;

    @FXML
    protected ListView<String> seriesList;

    @FXML
    protected VBox instructionSeriesPanel;

    @FXML
    protected Button addFolder;

    @FXML
    protected Button removeFolder;

    @InjectViewModel
    private LibraryViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing comic reader view");

        viewModel.getValueToAddProperty().set(resourceBundle.getString(DefaultValuesHelper.SCANNED_FOLDERS_LIST_DEFAULT));
        viewModel.getDefaultTextScannedFolderProperty().set(resourceBundle.getString(DefaultValuesHelper.SCANNED_FOLDERS_LIST_DEFAULT));
        scannedFoldersList.itemsProperty().bind(viewModel.getScannedFoldersList());
        scannedFoldersList.prefHeightProperty().bind(Bindings.size(viewModel.getScannedFoldersList().getValue()).multiply(LIST_CELL_HEIGHT));
        seriesList.itemsProperty().bind(viewModel.seriesListProperty());

        seriesList.visibleProperty().bind(viewModel.seriesVisibleProperty());
        seriesList.managedProperty().bind(viewModel.seriesVisibleProperty());
        instructionSeriesPanel.visibleProperty().bind(viewModel.seriesInstructionsVisibleProperty());
        instructionSeriesPanel.managedProperty().bind(viewModel.seriesInstructionsVisibleProperty());

        viewModel.getUpdateScannedFoldersListCommand().execute();
    }

    @Override
    public void openFileAction() {

    }

    @FXML
    public void addFolder(){
        Optional<String> firstValue = viewModel.getScannedFoldersList().get().stream().reduce((first, second) -> second);

        File initialFolder = null;
        if (firstValue.isPresent()) {
            initialFolder = validatePath(FilenameUtils.getFullPathNoEndSeparator(firstValue.get()));
        }

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(resourceBundle.getString(DefaultValuesHelper.DIRECTORY_CHOOSER_TEXT_KEY));
        chooser.setInitialDirectory(initialFolder);

        File selectedDirectory = chooser.showDialog(wrapper.getScene().getWindow());

        if (selectedDirectory != null && !scannedFoldersList.getItems().contains(selectedDirectory.getAbsolutePath())) {
            viewModel.getValueToAddProperty().set(selectedDirectory.getAbsolutePath());
            viewModel.getUpdateScannedFoldersListCommand().execute();
        }
    }

    @FXML
    public void removeFolder(ActionEvent actionEvent) {
        int selectedItem = scannedFoldersList.getSelectionModel().getSelectedIndex();

        String valueToRemove = viewModel.getScannedFoldersList().get().get(selectedItem);
        viewModel.getValueToRemoveProperty().set(valueToRemove);

        viewModel.getDeleteIndexScannedFoldersListCommand().execute();
    }

    private File validatePath (String path) {
        File file;

        if (path.isEmpty() || path.equals(resourceBundle.getString(DefaultValuesHelper.SCANNED_FOLDERS_LIST_DEFAULT))) {
            file = new File(System.getProperty(DefaultValuesHelper.HOME_FOLDER_KEY));
        } else {
            file = new File(path);
        }

        return file;
    }
}
