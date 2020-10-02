package com.mythicalcreaturesoftware.splash.ui;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.mythicalcreaturesoftware.splash.app.ComicReaderApp;
import com.mythicalcreaturesoftware.splash.controls.PreviewPopOver;
import com.mythicalcreaturesoftware.splash.event.MessageEvent;
import com.mythicalcreaturesoftware.splash.ui.viewmodel.ComicReaderViewModel;
import com.mythicalcreaturesoftware.splash.utils.ComponentHelper;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import com.mythicalcreaturesoftware.splash.utils.IconHelper;
import com.mythicalcreaturesoftware.splash.utils.PopupHelper;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ComicReaderView implements FxmlView<ComicReaderViewModel>, Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ComicReaderView.class);

    @FXML
    private VBox wrapper;

    @FXML
    private AnchorPane mainBar;

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
    private Button defaultScale;

    @InjectViewModel
    private ComicReaderViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    private PreviewPopOver popOver;
    private boolean isActive = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing comic reader view");

        popOver = new PreviewPopOver();

        initDisableBindings();
        initUiComponentsBindings();
        initImageViewer();
        initListeners();
        initMnemonics();

        wrapper.setFocusTraversable(true);
        wrapper.requestFocus();

        viewModel.subscribe(ComicReaderViewModel.OPEN_ALERT, (key, payload) -> {
            PopupHelper.showErrorPopup(this.mainImageContainer.getScene(), (String) payload[0], Arrays.toString((StackTraceElement[])payload[1]));
        });
    }

    private void initImageViewer() {
        leftImageViewer.imageProperty().bind(viewModel.getLeftImageProperty());
        rightImageViewer.imageProperty().bind(viewModel.getRightImageProperty());

        borderPane.rightProperty().bind(Bindings.when(viewModel.getIsTwoPagesProperty()).then(rightImageViewer).otherwise(IconHelper.getNullImageView()));

        mainImageContainer.minHeightProperty().bindBidirectional(viewModel.getScreenHeightProperty());
        mainImageContainer.minWidthProperty().bindBidirectional(viewModel.getScreenWidthProperty());

        mainImageContainer.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(), scrollPane.viewportBoundsProperty()));
        mainImageContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getHeight(), scrollPane.viewportBoundsProperty()));
    }

    private void initDisableBindings() {
        nextPage.disableProperty().bind(viewModel.getEnableAll().not());
        readingDirection.disableProperty().bind(viewModel.getEnableAll().not());
        pagePerView.disableProperty().bind(viewModel.getEnableAll().not());
        fullscreen.disableProperty().bind(viewModel.getEnableAll().not());
        defaultScale.disableProperty().bind(viewModel.getEnableAll().not());

        previousPage.disableProperty().bind(viewModel.getPreviousPageCommand().executableProperty().not());
        nextPage.disableProperty().bind(viewModel.getNextPageCommand().executableProperty().not());

        BooleanBinding enableZoomIn = Bindings.when(viewModel.getEnableAll().not().or(viewModel.getScaleLevelProperty().greaterThanOrEqualTo(DefaultValuesHelper.MAXIMUM_SCALE_LEVEL))).then(true).otherwise(false);
        zoomIn.disableProperty().bind(enableZoomIn);

        BooleanBinding enableZoomOut = Bindings.when(viewModel.getEnableAll().not().or(viewModel.getScaleLevelProperty().lessThan(DefaultValuesHelper.MINIMUM_SCALE_LEVEL))).then(true).otherwise(false);
        zoomOut.disableProperty().bind(enableZoomOut);
    }

    private void initUiComponentsBindings() {
        StringBinding headerBinding = Bindings.when(viewModel.getFileNameProperty().isNotEqualTo("")).then(viewModel.getFileNameProperty()).otherwise(resourceBundle.getString(DefaultValuesHelper.HEADER_TEXT_KEY));
        headerButton.textProperty().bind(headerBinding);

        DoubleBinding scaleBinding = viewModel.getScaleLevelProperty().multiply(100);
        zoomPercentageLabel.textProperty().bind(Bindings.concat(Bindings.format("%.0f", scaleBinding), " %"));
        pageIndicatorLabel.textProperty().bind(Bindings.concat(resourceBundle.getString(DefaultValuesHelper.PAGE_TEXT_KEY), " ", viewModel.getCurrentPageProperty(), "/", viewModel.getTotalPagesProperty()));

        pageSelector.maxProperty().bindBidirectional(viewModel.getTotalPagesProperty());
        pageSelector.valueProperty().bindBidirectional(viewModel.getCurrentPageProperty());

        ObjectBinding<Node> readingDirectionChanged = Bindings.when(viewModel.getReadingDirectionRightProperty().not()).then(IconHelper.createReadingDirectionIconProperty(true)).otherwise(IconHelper.createReadingDirectionIconProperty(false));
        readingDirection.graphicProperty().bind(readingDirectionChanged);

        ObjectBinding<Node> pageSelectorBinding = Bindings.when(viewModel.getIsTwoPagesProperty().not()).then(IconHelper.createSimplePageIconProperty()).otherwise(IconHelper.createDoublePageIconProperty());
        pagePerView.graphicProperty().bind(pageSelectorBinding);

        ObjectBinding<Node> defaultScaleBinding = Bindings.when(viewModel.getCurrentPageDefaultScaleLevelProperty().greaterThanOrEqualTo(viewModel.getScaleLevelProperty())).then(IconHelper.createExpandScaleIconProperty()).otherwise(IconHelper.createCollapseScaleIconProperty());
        defaultScale.graphicProperty().bind(defaultScaleBinding);

        popOver.getScaleLevelProperty().bind(viewModel.getCurrentPageDefaultScaleLevelProperty());
        popOver.getSizeProperty().bind(viewModel.getFirstPageDimensionProperty());
        popOver.getImageView().imageProperty().bind(viewModel.getPreviewImageProperty());
    }

    private void initListeners() {
        viewModel.getOpenFileCommand().runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ComicReaderApp.instance().showLoading();
            }
            if (!newValue) {
                ComicReaderApp.instance().hideLoading();
            }
        });

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

        pageSelector.maxProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0) {
                pageSelector.valueChangingProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if (newValue1) {
                        popOver.show(pageIndicatorLabel);
                    } else {
                        popOver.hide();
                    }
                });

                pageSelector.valueProperty().addListener((observable1, oldValue1, newValue1) -> {
                    viewModel.getCurrentPagePreviewProperty().setValue(newValue1);
                    viewModel.getUpdatePreviewImageCommand().execute();
                });
            }
        });
    }

    private void initMnemonics() {
        wrapper.sceneProperty().addListener((observable, oldValue, newValue) -> {
            newValue.addEventFilter(MessageEvent.OPEN_FILE_EVENT, event -> {
                logger.debug("Receiving open file event");

                if (isActive) {
                    openFileAction();
                }
            });

            newValue.addEventFilter(MessageEvent.PREVIOUS_PAGE_EVENT, event -> {
                logger.debug("Receiving previous event");

                if (isActive) {
                    previousPageAction();
                }
            });

            newValue.addEventFilter(MessageEvent.NEXT_PAGE_EVENT, event -> {
                logger.debug("Receiving next event");

                if (isActive) {
                    nextPageAction();
                }
            });

            newValue.addEventFilter(MessageEvent.CHANGE_READING_DIRECTION_EVENT, event -> {
                logger.debug("Receiving change reading direction event");

                if (isActive) {
                    readingDirectionAction();
                }
            });

            newValue.addEventFilter(MessageEvent.CHANGE_PAGES_PER_VIEW_EVENT, event -> {
                logger.debug("Receiving change pages per view event");

                if (isActive) {
                    pagePerViewAction();
                }
            });

            newValue.addEventFilter(MessageEvent.ZOOM_IN_EVENT, event -> {
                logger.debug("Receiving zoom in event");

                if (isActive) {
                    zoomInAction();
                }
            });

            newValue.addEventFilter(MessageEvent.ZOOM_OUT_EVENT, event -> {
                logger.debug("Receiving zoom out event");

                if (isActive) {
                    zoomOutAction();
                }
            });

            newValue.addEventFilter(MessageEvent.SET_DEFAULT_SCALE_EVENT, event -> {
                logger.debug("Receiving set default scale event");

                if (isActive) {
                    setDefaultScale();
                }
            });

            newValue.addEventFilter(MessageEvent.FULLSCREEN_EVENT, event -> {
                logger.debug("Receiving fullscreen event");

                if (isActive) {
                    fullscreenAction();
                }
            });

            newValue.addEventFilter(MessageEvent.EXITED_FULLSCREEN_EVENT, event -> {
                logger.debug("Receiving fullscreen event");

                if (isActive) {
                    viewModel.getRefreshFileCommand().execute();
                }
            });
        });
    }

    @FXML
    public void openFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(resourceBundle.getString(DefaultValuesHelper.FILE_CHOOSER_TEXT_KEY));
        chooser.setInitialDirectory(
            new File(System.getProperty(DefaultValuesHelper.HOME_FOLDER_KEY))
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
            openFile(file.getAbsolutePath());
        }
    }

    public void openFile(String path) {
        try {
            viewModel.getFilePathProperty().setValue(path);
            viewModel.getOpenFileCommand().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void previousPageAction() {
        if (viewModel.getEnableAll().getValue() && viewModel.getLoadPreviousPageCommand().isExecutable()) {
            viewModel.getLoadPreviousPageCommand().execute();
        }
    }

    @FXML
    public void nextPageAction() {
        if (viewModel.getEnableAll().getValue() && viewModel.getLoadNextPageCommand().isExecutable()) {
            viewModel.getLoadNextPageCommand().execute();
        }
    }

    @FXML
    public void readingDirectionAction() {
        if (viewModel.getEnableAll().getValue()) {
            viewModel.getReadingDirectionCommand().execute();
        }
    }

    @FXML
    public void pagePerViewAction() {
        if (viewModel.getEnableAll().getValue()) {
            viewModel.getUpdatePagesPerViewPageCommand().execute();
        }
    }

    @FXML
    public void zoomInAction() {
        if (viewModel.getEnableAll().getValue() && viewModel.getZoomOutCommand().isExecutable()) {
            viewModel.getZoomInCommand().execute();
        }
    }

    @FXML
    public void zoomOutAction() {
        if (viewModel.getEnableAll().getValue() && viewModel.getZoomOutCommand().isExecutable()) {
            viewModel.getZoomOutCommand().execute();
        }
    }

    @FXML
    public void setDefaultScale() {
        if (viewModel.getEnableAll().getValue()) {
            viewModel.getApplyDefaultScaleCommand().execute();
        }
    }

    @FXML
    public void fullscreenAction() {
        if (!fullscreen.isDisabled()) {
            ComicReaderApp.instance().showFullscreen();
        }
    }

    @FXML
    public void minimizeAction() {
        BorderlessScene scene = (BorderlessScene) minimizeButton.getScene();
        scene.minimizeStage();
    }

    @FXML
    public void maximizeAction() {
        BorderlessScene scene = (BorderlessScene) maximizeButton.getScene();
        scene.maximizeStage();
    }

    @FXML
    public void closeAction() {
        Platform.exit();
        System.exit(0);
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Node getMainBar() {
        return mainBar;
    }
}
