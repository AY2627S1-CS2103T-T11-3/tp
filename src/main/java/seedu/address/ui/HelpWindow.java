package seedu.address.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for the offline command reference.
 */
public class HelpWindow extends UiPart<Stage> {

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private VBox commandEntries;

    /**
     * Creates a new HelpWindow using the given stage.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        getRoot().initModality(Modality.NONE);
        commandEntries.lookupAll(".text-area").forEach(node -> sizeToWrappedText((TextArea) node));
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Fits selectable text to its wrapped height, so the outer reference scrolls as one page.
     * The extra space allows for the text area's padding, border and caret.
     */
    private void sizeToWrappedText(TextArea area) {
        // Only the outer reference should scroll; inner scrollbars change the available wrapping width.
        area.skinProperty().addListener((observable, oldSkin, newSkin) -> {
            if (newSkin != null) {
                ScrollPane scroll = (ScrollPane) area.lookup(".scroll-pane");
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        });
        area.setMinHeight(Region.USE_PREF_SIZE);
        area.setMaxHeight(Region.USE_PREF_SIZE);
        area.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
            Text measurement = new Text(area.getText());
            measurement.setFont(area.getFont());
            measurement.setWrappingWidth(Math.max(1, area.getWidth() - 36));
            return Math.ceil(measurement.getLayoutBounds().getHeight()) + 32;
        }, area.widthProperty(), area.fontProperty(), area.textProperty()));
        // Wrapping can change during a layout pass; apply the resulting height on the following pass.
        area.prefHeightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> area.getParent().requestLayout()));
    }

    /**
     * Shows the offline command reference.
     */
    public void show() {
        logger.fine("Showing the offline command reference.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    @FXML
    public void hide() {
        getRoot().hide();
    }

    /**
     * Restores the existing help window and brings it to the foreground.
     */
    public void focus() {
        getRoot().setIconified(false);
        getRoot().toFront();
        getRoot().requestFocus();
    }
}
