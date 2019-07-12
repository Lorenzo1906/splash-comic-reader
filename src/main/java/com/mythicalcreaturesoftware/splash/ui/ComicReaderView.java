package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.utils.ComponentHelper;
import com.mythicalcreaturesoftware.splash.utils.DefaultValues;
import com.mythicalcreaturesoftware.splash.utils.IconHelper;
import com.mythicalcreaturesoftware.splash.utils.ScreenHelper;
import com.sun.javafx.binding.SelectBinding;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ComicReaderView implements FxmlView<ComicReaderViewModel>, Initializable {

    private static Logger logger = LoggerFactory.getLogger(ComicReaderView.class);

    @FXML
    private VBox wrapper;

    @FXML
    private StackPane mainImageContainer;

    @FXML
    private ImageView leftImageViewer;

    @FXML
    private ImageView rightImageViewer;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Slider pageSelector;

    @FXML
    private Label zoomPercentageLabel;

    @FXML
    private Label pageIndicatorLabel;

    @FXML
    private Button headerButton;

    @FXML
    private Button previousPage;

    @FXML
    private Button nextPage;

    @FXML
    private Button readingDirection;

    @FXML
    private Button pagePerView;

    @FXML
    private Button zoomIn;

    @FXML
    private Button zoomOut;

    @FXML
    private Button fullscreen;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button maximizeButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button defaultScale;

    @InjectViewModel
    private ComicReaderViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing comic reader view");

        initializeDisableBindings();
        initializeUiComponentsBindings();
        initializeImageViewer();
        initializeListeners();
        initializeMnemonics();

        wrapper.setFocusTraversable(true);
        wrapper.requestFocus();
    }

    private void initializeImageViewer () {
        leftImageViewer.imageProperty().bind(viewModel.getLeftImageProperty());
        rightImageViewer.imageProperty().bind(viewModel.getRightImageProperty());

        borderPane.rightProperty().bind(Bindings.when(viewModel.getIsTwoPagesProperty()).then(rightImageViewer).otherwise(IconHelper.getNullImageView()));

        mainImageContainer.minHeightProperty().bindBidirectional(viewModel.getScreenHeightProperty());
        mainImageContainer.minWidthProperty().bindBidirectional(viewModel.getScreenWidthProperty());

        mainImageContainer.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(), scrollPane.viewportBoundsProperty()));
        mainImageContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getHeight(), scrollPane.viewportBoundsProperty()));
    }

    private void initializeDisableBindings () {
        nextPage.disableProperty().bind(viewModel.getEnableAll().not());
        readingDirection.disableProperty().bind(viewModel.getEnableAll().not());
        pagePerView.disableProperty().bind(viewModel.getEnableAll().not());
        fullscreen.disableProperty().bind(viewModel.getEnableAll().not());
        defaultScale.disableProperty().bind(viewModel.getEnableAll().not());

        previousPage.disableProperty().bind(viewModel.getPreviousPageCommand().executableProperty().not());
        nextPage.disableProperty().bind(viewModel.getNextPageCommand().executableProperty().not());

        BooleanBinding enableZoomIn = Bindings.when(viewModel.getEnableAll().not().or(viewModel.getScaleLevelProperty().greaterThanOrEqualTo(DefaultValues.MAXIMUM_SCALE_LEVEL))).then(true).otherwise(false);
        zoomIn.disableProperty().bind(enableZoomIn);

        BooleanBinding enableZoomOut = Bindings.when(viewModel.getEnableAll().not().or(viewModel.getScaleLevelProperty().lessThan(DefaultValues.MINIMUM_SCALE_LEVEL))).then(true).otherwise(false);
        zoomOut.disableProperty().bind(enableZoomOut);
    }

    private void initializeUiComponentsBindings () {
        StringBinding headerBinding = Bindings.when(viewModel.getFileNameProperty().isNotEqualTo("")).then(viewModel.getFileNameProperty()).otherwise(resourceBundle.getString(DefaultValues.HEADER_TEXT_KEY));
        headerButton.textProperty().bind(headerBinding);

        DoubleBinding scaleBinding = viewModel.getScaleLevelProperty().multiply(100);
        IntegerBinding integerBinding = new SelectBinding.AsInteger(scaleBinding);
        zoomPercentageLabel.textProperty().bind(Bindings.concat(integerBinding, " %"));
        pageIndicatorLabel.textProperty().bind(Bindings.concat(resourceBundle.getString(DefaultValues.PAGE_TEXT_KEY), " ", viewModel.getCurrentPageProperty(), "/", viewModel.getTotalPagesProperty()));

        pageSelector.maxProperty().bindBidirectional(viewModel.getTotalPagesProperty());
        pageSelector.valueProperty().bindBidirectional(viewModel.getCurrentPageProperty());

        ObjectBinding<Node> readingDirectionChanged = Bindings.when(viewModel.getReadingDirectionRightProperty().not()).then(IconHelper.createReadingDirectionIconProperty(true)).otherwise(IconHelper.createReadingDirectionIconProperty(false));
        readingDirection.graphicProperty().bind(readingDirectionChanged);

        ObjectBinding<Node> pageSelectorBinding = Bindings.when(viewModel.getIsTwoPagesProperty().not()).then(IconHelper.createSimplePageIconProperty()).otherwise(IconHelper.createDoublePageIconProperty());
        pagePerView.graphicProperty().bind(pageSelectorBinding);

        ObjectBinding<Node> defaultScaleBinding = Bindings.when(viewModel.getCurrentPageDefaultScaleLevelProperty().greaterThanOrEqualTo(viewModel.getScaleLevelProperty())).then(IconHelper.createExpandScaleIconProperty()).otherwise(IconHelper.createCollapseScaleIconProperty());
        defaultScale.graphicProperty().bind(defaultScaleBinding);
    }

    private void initializeListeners () {
        viewModel.getLeftImageDimensionProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue != null) {
                ComponentHelper.setImageViewSize(leftImageViewer, newValue, viewModel.getScaleLevelProperty().doubleValue());
            }
        });

        viewModel.getRightImageDimensionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ComponentHelper.setImageViewSize(rightImageViewer, newValue, viewModel.getScaleLevelProperty().doubleValue());
            }
        });

        viewModel.getScaleLevelProperty().addListener((observable, oldValue, newValue) -> {
            ComponentHelper.setImageViewSize(rightImageViewer, viewModel.getRightImageDimensionProperty().get(), newValue.doubleValue());
            ComponentHelper.setImageViewSize(leftImageViewer, viewModel.getLeftImageDimensionProperty().get(), newValue.doubleValue());
        });

        pageSelector.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                viewModel.getCurrentPageProperty().setValue(pageSelector.getValue());
                viewModel.getLoadSliderPageCommand().execute();
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, event -> {
            event.consume();

            if (event.getDeltaY() > 0) {
                viewModel.getZoomInCommand().execute();
            }
            if (event.getDeltaY() < 0) {
                viewModel.getZoomOutCommand().execute();
            }
        });
    }

    public void initializeMnemonics () {
        wrapper.sceneProperty().addListener((observable, oldValue, newValue) -> {
            KeyCombination headerKeyCombination = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
            KeyCombination previousPageKeyCombination = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_ANY);
            KeyCombination nextPageKeyCombination = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_ANY);
            KeyCombination readingDirectionKeyCombination = new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_ANY);
            KeyCombination pagePerViewKeyCombination = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_ANY);
            KeyCombination zoomOutKeyCombination = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_ANY);
            KeyCombination zoomInKeyCombination = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_ANY);
            KeyCombination defaultScaleKeyCombination = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_ANY);
            KeyCombination fullscreenKeyCombination = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_ANY);

            newValue.setOnKeyPressed(event -> {
                if (headerKeyCombination.match(event)) {
                    openFileAction();
                }
                if (previousPageKeyCombination.match(event)) {
                    previousPageAction();
                }
                if (nextPageKeyCombination.match(event)) {
                    nextPageAction();
                }
                if (readingDirectionKeyCombination.match(event)) {
                    readingDirectionAction();
                }
                if (pagePerViewKeyCombination.match(event)) {
                    pagePerViewAction();
                }
                if (zoomOutKeyCombination.match(event)) {
                    zoomOutAction();
                }
                if (zoomInKeyCombination.match(event)) {
                    zoomInAction();
                }
                if (defaultScaleKeyCombination.match(event)) {
                    setDefaultScale();
                }
                if (fullscreenKeyCombination.match(event)) {
                    fullscreenAction();
                }
            });

        });
    }

    @FXML
    public void openFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(resourceBundle.getString(DefaultValues.FILE_CHOOSER_TEXT_KEY));
        chooser.setInitialDirectory(
            new File(System.getProperty(DefaultValues.HOME_FOLDER_KEY))
        );
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter(resourceBundle.getString("fileChooser.allFiles"), "*.cbr", "*.cbr", "*.cbz", "*.cbt", "*.cb7", "*.cba", "*.CBR", "*.CBZ", "*.CBT", "*.CB7", "*.CBA"),
            new FileChooser.ExtensionFilter("CBR", "*.cbr", "*.CBR"),
            new FileChooser.ExtensionFilter("CBZ", "*.cbz", "*.CBZ"),
            new FileChooser.ExtensionFilter("CBT", "*.cbt", "*.CBT"),
            new FileChooser.ExtensionFilter("CB7", "*.cb7", "*.CB7"),
            new FileChooser.ExtensionFilter("CBA", "*.cba", "*.CBA")
        );

        File file =  chooser.showOpenDialog(headerButton.getScene().getWindow());
        if (file != null) {
            viewModel.getFilePathProperty().setValue(file.getAbsolutePath());
            viewModel.getScaleLevelProperty().setValue(1);
            viewModel.getOpenFileCompleteCommand().execute();
        }
    }

    @FXML
    public void previousPageAction() {
        viewModel.getLoadPreviousPageCommand().execute();
    }

    @FXML
    public void nextPageAction() {
        viewModel.getLoadNextPageCommand().execute();
    }

    @FXML
    public void readingDirectionAction() {
        viewModel.getReadingDirectionCommand().execute();
    }

    @FXML
    public void pagePerViewAction() {
        viewModel.getUpdatePagesPerViewPageCommand().execute();
    }

    @FXML
    public void zoomInAction() {
        viewModel.getZoomInCommand().execute();
    }

    @FXML
    public void zoomOutAction() {
        viewModel.getZoomOutCommand().execute();
    }

    @FXML
    public void setDefaultScale() {
        viewModel.getApplyDefaultScaleCommand().execute();
    }

    @FXML
    public void fullscreenAction() {
        logger.info("Full screen Action");
    }

    @FXML
    public void minimizeAction() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void maximizeAction() {
        Stage stage = (Stage) maximizeButton.getScene().getWindow();

        if (ScreenHelper.isMaximized()) {
            ScreenHelper.unMaximize(stage);
        } else {
            ScreenHelper.maximize(stage);
        }

        System.out.println(maximizeButton.getScene().getMnemonics());
    }

    @FXML
    public void closeAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
