package com.mythicalcreaturesoftware.splash.ui;

import com.mythicalcreaturesoftware.splash.utils.IconHelper;
import com.mythicalcreaturesoftware.splash.utils.ScreenHelper;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
    private StackPane mainImageContainer;

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

    @InjectViewModel
    private ComicReaderViewModel viewModel;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing comic reader view");

        initializeUiComponentsBindings();

        Image image = new Image("http://images6.fanpop.com/image/photos/40700000/Melissa-Benoist-supergirl-2015-tv-series-40753099-1280-1920.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        imageView.setFitHeight(600);
        imageView.setFitWidth(800);

        Image image2 = new Image("https://www.hawtcelebs.com/wp-content/uploads/2017/07/melissa-benoist-on-the-set-of-supergirl-in-new-westminster-07-27-2017_6.jpg");
        ImageView imageView2 = new ImageView(image2);
        imageView2.setPreserveRatio(true);

        imageView2.setFitHeight(600);
        imageView2.setFitWidth(800);

        borderPane.setLeft(imageView);
        borderPane.setRight(imageView2);

        mainImageContainer.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(), scrollPane.viewportBoundsProperty()));
        mainImageContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getHeight(), scrollPane.viewportBoundsProperty()));
    }

    private void initializeUiComponentsBindings () {
        StringBinding headerBinding = Bindings.when(viewModel.getFileNameProperty().isNotEqualTo("")).then(viewModel.getFileNameProperty()).otherwise(resourceBundle.getString("header.default.text"));
        headerButton.textProperty().bind(headerBinding);

        zoomPercentageLabel.textProperty().bind(Bindings.concat(viewModel.getZoomLevelProperty(), " %"));
        pageIndicatorLabel.textProperty().bind(Bindings.concat(resourceBundle.getString("ui.page"), " ", viewModel.getCurrentPageProperty(), "/", viewModel.getTotalPagesProperty()));

        previousPage.disableProperty().bind(viewModel.getPreviousPageCommand().executableProperty().not());
        nextPage.disableProperty().bind(viewModel.getNextPageCommand().executableProperty().not());

        pageSelector.setMax(viewModel.getTotalPagesProperty().doubleValue());
        pageSelector.valueProperty().bindBidirectional(viewModel.getCurrentPageProperty());
        pageSelector.valueProperty().addListener((ov, oldVal, newVal) -> {
            logger.debug("Updating current page");
            viewModel.getCurrentPageProperty().setValue(newVal);
        });

        ObjectBinding<Node> readingDirectionChanged = Bindings.when(viewModel.getReadingDirectionRightProperty().not()).then(IconHelper.createReadingDirectionIconProperty(true)).otherwise(IconHelper.createReadingDirectionIconProperty(false));
        readingDirection.graphicProperty().bind(readingDirectionChanged);

        ObjectBinding<Node> pageSelectorBinding = Bindings.when(viewModel.getIsTwoPagesProperty().not()).then(IconHelper.createSimplePageIconProperty()).otherwise(IconHelper.createDoublePageIconProperty());
        pagePerView.graphicProperty().bind(pageSelectorBinding);
    }

    @FXML
    public void openFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(resourceBundle.getString("fileChooser.title"));
        chooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
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
            viewModel.getOpenFileCommand().execute();
        }
    }

    @FXML
    public void previousPageAction() {
        viewModel.getPreviousPageCommand().execute();
    }

    @FXML
    public void nextPageAction() {
        viewModel.getNextPageCommand().execute();
    }

    @FXML
    public void readingDirectionAction() {
        viewModel.getReadingDirectionCommand().execute();
    }

    @FXML
    public void pagePerViewAction() {
        viewModel.getPagePerViewCommand().execute();
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
    }

    @FXML
    public void closeAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
