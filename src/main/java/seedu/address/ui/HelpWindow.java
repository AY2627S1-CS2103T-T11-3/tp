package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for the offline command reference.
 */
public class HelpWindow extends UiPart<Stage> {

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    /**
     * Creates a new HelpWindow using the given stage.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        getRoot().initModality(Modality.NONE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
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
